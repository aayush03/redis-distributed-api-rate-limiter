package org.aayush.cache.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author Aayush Srivastava
 */
@Configuration
public class RedisConfig {

    @Value(value = "${redis.sentinel.master}")
    private String redisSentinelMaster;

    @Value(value = "${redis.sentinel.host.and.ports}")
    private String redisSentinelHostAndPorts;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public RedisConfig() {
        logger.info("inside redis configure constructor");
    }

    public static boolean isNullObject(Object obj) {
        return (null == obj);
    }

    @Bean
    @Primary
    public RedisConnectionFactory jedisConnectionFactory() {
        RedisSentinelConfiguration redisSentinelConfiguration = new RedisSentinelConfiguration();
        redisSentinelConfiguration.master(redisSentinelMaster);
        logger.info("REDIS_SENTINEL_MASTER::{}", redisSentinelMaster);
        logger.info("redisSentinelHostAndPorts::{}", redisSentinelHostAndPorts);
        if (!isNullObject(redisSentinelHostAndPorts)) {
            HostAndPort hostAndPort = null;
            if (redisSentinelHostAndPorts.contains(";")) {
                for (String node : redisSentinelHostAndPorts.split(";")) {
                    if (null != node & node.contains(",")) {
                        hostAndPort = new HostAndPort(node);
                        logger.info(
                                "Host:Port::{}:{}", hostAndPort.getHost(), hostAndPort.getPort());
                        redisSentinelConfiguration.sentinel(hostAndPort.getHost(), hostAndPort.getPort());
                    }
                }
            } else {
                if (redisSentinelHostAndPorts.contains(",")) {
                    hostAndPort = new HostAndPort(redisSentinelHostAndPorts);
                    logger.info(
                            "fallback Host:Port::{}:{}", hostAndPort.getHost(), hostAndPort.getPort());
                    redisSentinelConfiguration.sentinel(hostAndPort.getHost(), hostAndPort.getPort());
                }
            }
        } else {
            logger.info("Host and Ports are not specified.");
        }
        return new JedisConnectionFactory(redisSentinelConfiguration);
    }

    @Bean("redisTemplate")
    public RedisTemplate redisTemplate() {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        redisTemplate.setKeySerializer(stringRedisSerializer());
        redisTemplate.setHashKeySerializer(stringRedisSerializer());
        return redisTemplate;
    }

    @Bean("longStringHashRedisTemplate")
    public RedisTemplate longStringHashRedisTemplate() {
        final RedisTemplate<String, Long> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        redisTemplate.setHashValueSerializer(new GenericToStringSerializer<>(Long.class));
        redisTemplate.setKeySerializer(stringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericToStringSerializer<>(Long.class));
        redisTemplate.setHashKeySerializer(stringRedisSerializer());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public StringRedisSerializer stringRedisSerializer() {
        return new StringRedisSerializer();
    }

    private class HostAndPort {
        private String host;
        private int port;
        private String[] hostAndPortStringArray = new String[2];

        HostAndPort(String hostAndPort) {
            hostAndPortStringArray = hostAndPort.split(",");
            host = hostAndPortStringArray[0];
            port = Integer.parseInt(hostAndPortStringArray[1]);
        }

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }
    }
}
