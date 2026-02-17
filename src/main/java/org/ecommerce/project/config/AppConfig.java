package org.ecommerce.project.config;

import org.ecommerce.project.redis.rate_limiter.RateLimiterFilter;
import org.ecommerce.project.redis.rate_limiter.RateLimiterService;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public RateLimiterFilter rateLimiterFilter(RateLimiterService rateLimiterService) {
        return new RateLimiterFilter(rateLimiterService);
    }
}
