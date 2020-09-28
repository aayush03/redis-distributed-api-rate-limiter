package org.aayush.cache;


import java.util.concurrent.TimeUnit;

/**
 * @author Aayush Srivastava
 */
public interface CacheTemplate {

    void putValue(final String key, final Object value, final String cacheName);

    Object getValue(String key, String cacheName);

    Object getValue(final String key);

    void clearAllCache(final String cacheName);

    void deleteValue(final String key);

    void deleteValue(String key, String cacheName);

    boolean isRateLimitExhaustedInSlidingWindow(String rateLimitingKey, long allowedRequests, long slidingWindowTimeout, TimeUnit unit);
}
