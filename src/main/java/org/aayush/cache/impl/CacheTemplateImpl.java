package org.aayush.cache.impl;

import org.aayush.cache.CacheTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Aayush Srivastava
 */
@Service("cacheTemplate")
public class CacheTemplateImpl implements CacheTemplate {

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate redisTemplate;
    @Autowired
    @Qualifier("longStringHashRedisTemplate")
    private RedisTemplate longStringHashRedisTemplate;

    private Logger logger = LoggerFactory.getLogger(CacheTemplateImpl.class);

    @Override
    public boolean isRateLimitExhaustedInSlidingWindow(String rateLimitingKey, long allowedRequests, long slidingWindowTimeout, TimeUnit unit) {
        HashOperations<String, String, Long> hashOperations = longStringHashRedisTemplate.opsForHash();

        Long count = hashOperations.get(rateLimitingKey, "");

        if (null != count && count >= allowedRequests) {
            return true;
        }
        List<Object> transactionResults = (List<Object>) longStringHashRedisTemplate.execute(getOperationsInTransaction(rateLimitingKey, slidingWindowTimeout, unit, hashOperations));
        logger.info("Current count of API hits attempted :: {}", null != transactionResults ? transactionResults.get(0) : null);
        return false;
    }

    private SessionCallback<Object> getOperationsInTransaction(String rateLimitingKey, long slidingWindowTimeout, TimeUnit unit, HashOperations<String, String, Long> hashOperations) {
        return new SessionCallback<Object>() {
            @Override
            public <K, V> List<Object> execute(RedisOperations<K, V> redisOperations) throws DataAccessException {
                longStringHashRedisTemplate.multi();
                hashOperations.increment(rateLimitingKey, "", 1); //incrementing counter of API hits attempted within the sliding window duration 1 by 1
                longStringHashRedisTemplate.expire(rateLimitingKey, slidingWindowTimeout, unit);
                return longStringHashRedisTemplate.exec(); //This contains the results of all operations completed within the transaction
            }
        };
    }
}