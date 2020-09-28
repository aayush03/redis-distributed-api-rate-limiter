package org.aayush.service;

import org.aayush.cache.api.rate.limiter.annotation.DistributedRateLimiter;
import org.aayush.model.MockModel;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author Aayush Srivastava
 */
@Service
public class MockServiceImpl implements MockService {

    @Override
    @DistributedRateLimiter(
            prefix = "redis.distributed.rate.limiter.prefix",
            sleepingTimeInString = "redis.distributed.rate.limiter.sleepingTimeInMilliSeconds",
            slidingWindowTimeoutInString = "redis.distributed.rate.limiter.slidingWindowTimeInMilliSeconds",
            allowedRequestsInString = "redis.distributed.rate.limiter.request.count.allowed",
            unit = TimeUnit.MILLISECONDS
    )
    public String getMockValue(String key) {
        return getMockedString(key, new MockModel(1, "Aayush Srivastava"));
    }


    private String getMockedString(String key, MockModel model) {
        StringBuilder builder = new StringBuilder();
        builder.append(key)
                .append("::")
                .append(model);
        return builder.toString();
    }
}
