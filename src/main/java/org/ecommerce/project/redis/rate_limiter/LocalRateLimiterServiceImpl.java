package org.ecommerce.project.redis.rate_limiter;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Profile("k8s")
public class LocalRateLimiterServiceImpl implements RateLimiterService {

    private final Map<String, Integer> requestCounts = new ConcurrentHashMap<>();
    private final int LIMIT = 100;

    @Override
    public boolean isAllowed(String key, int capacity, int refillTokens, long refillSeconds) {

        requestCounts.putIfAbsent(key, 0);
        int count = requestCounts.get(key);

        if (count >= LIMIT) return false;

        requestCounts.put(key, count + 1);
        return true;
    }
}
