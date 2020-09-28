package org.aayush.cache.api.rate.limiter.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * @author Aayush Srivastava
 */

/**
 * Marks a method to be used as handler while implementing Redis Sliding Window Rate Limiter.
 *
 * <p>The method using {@link DistributedRateLimiter} must have the first
 * argument as the unique key to be used for attempting api rate limiting.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DistributedRateLimiter {

    /**
     * key prefix
     */
    String prefix() default "";

    /**
     * sliding window timeout of the rate limiter
     */
    long slidingWindowTimeout() default 2L;

    /**
     * sliding window timeout of the rate limiter in String
     */
    String slidingWindowTimeoutInString() default "";

    /**
     * time unit
     */
    TimeUnit unit() default TimeUnit.SECONDS;

    /**
     * number of API requests allowed in the sliding window by the rate limiter
     */
    long allowedRequests() default 2000L;

    /**
     * number of API requests allowed in the sliding window by the rate limiter in String
     */
    String allowedRequestsInString() default "";

    /**
     * sleep time for request if rate limit is exhausted
     */
    long sleepingTime() default 100000L;

    /**
     * sleep time for request if rate limit is exhausted in String
     */
    String sleepingTimeInString() default "";

}
