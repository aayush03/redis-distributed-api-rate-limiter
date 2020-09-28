package org.aayush.cache.api.rate.limiter.annotation;

import org.aayush.cache.CacheTemplate;
import org.aayush.cache.impl.CacheTemplateImpl;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * @author Aayush Srivastava
 */
@Aspect
@Component
public class DistributedRateLimiterAspect implements KeyGenerator {

    private Logger logger = LoggerFactory.getLogger(CacheTemplateImpl.class);
    @Autowired
    private CacheTemplate redisRateLimitingClient;
    @Autowired
    private Environment environment;

    /**
     * {@link DistributedRateLimiter}
     */
    @Pointcut(value = "execution(* *(..)) && @annotation(org.aayush.cache.api.rate.limiter.annotation.DistributedRateLimiter)")
    public void distributedRateLimiter() {
        logger.info("POINTCUT ACTIVATED");
    }

    /**
     * @param joinPoint
     * @param rateLimiter
     * @return
     * @throws Throwable
     */
    @Around(value = "distributedRateLimiter() && @annotation(rateLimiter)")
    public Object handle(ProceedingJoinPoint joinPoint, DistributedRateLimiter rateLimiter) throws Throwable {
        long start = System.nanoTime();
        String key = (String) joinPoint.getArgs()[0];

        long slidingWindowTimeout = parseLongWithDefaultValue(environment.getProperty(rateLimiter.slidingWindowTimeoutInString()), rateLimiter.slidingWindowTimeout());
        long sleepingTime = parseLongWithDefaultValue(environment.getProperty(rateLimiter.sleepingTimeInString()), rateLimiter.sleepingTime());
        long allowedRequests = parseLongWithDefaultValue(environment.getProperty(rateLimiter.allowedRequestsInString()), rateLimiter.allowedRequests());
        String rateLimitingKey = rateLimiter.prefix() + (key != null ? key : "");

        boolean exhausted = redisRateLimitingClient.isRateLimitExhaustedInSlidingWindow(rateLimitingKey, allowedRequests, slidingWindowTimeout, rateLimiter.unit());

        if (exhausted) {
            Thread.sleep(sleepingTime);
        }

        long end = System.nanoTime();
        logger.info("distributed rateLimiter cost: {} ns", end - start);
        return joinPoint.proceed();
    }

    private long parseLongWithDefaultValue(String stringToBeParsed, long defaultValue) {
        long val;
        try {
            val = Long.parseLong(stringToBeParsed);
        } catch (NumberFormatException e) {
            val = defaultValue;
        }
        return val;
    }

    @Override
    public Object generate(Object target, Method method, Object... params) {
        return target.getClass().getSimpleName() + "_"
                + method.getName() + "_"
                + StringUtils.arrayToDelimitedString(params, "_");
    }
}
