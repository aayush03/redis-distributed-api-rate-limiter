package org.aayush.cache;


import java.util.concurrent.TimeUnit;

/**
 * @author Aayush Srivastava
 */
public interface CacheTemplate {

    boolean isRateLimitExhaustedInSlidingWindow(String rateLimitingKey, long allowedRequests, long slidingWindowTimeout, TimeUnit unit);
}
