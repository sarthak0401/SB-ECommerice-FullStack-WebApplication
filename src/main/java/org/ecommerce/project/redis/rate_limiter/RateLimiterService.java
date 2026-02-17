package org.ecommerce.project.redis.rate_limiter;

public interface RateLimiterService {
    boolean isAllowed(String key, int capacity, int refillTokens, long refillSeconds);
}