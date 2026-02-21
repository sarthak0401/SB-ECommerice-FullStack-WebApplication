package org.ecommerce.project.redis.rate_limiter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Order(1)
public class RateLimiterFilter extends OncePerRequestFilter {

    private final RateLimiterService limiter;

    public RateLimiterFilter(RateLimiterService limiter) {
        this.limiter = limiter;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        String ip = request.getRemoteAddr();

        boolean allowed = true;

        try {
            if(uri.startsWith("/api/auth/signin")){
                allowed = limiter.isAllowed("login:" + ip, 5, 5, 60);
            } else if(uri.startsWith("/api/public")){
                allowed = limiter.isAllowed("public:" + ip, 60, 60, 60);
            } else {
                allowed = limiter.isAllowed("api:" + ip, 120, 120, 120);
            }
        } catch (Exception e) {
            // Log the error so you know Redis is down, but don't stop the request
            logger.error("Redis is unreachable. Bypassing rate limiter. Error: " + e.getMessage());
            allowed = true;
        }

        if(!allowed){
            response.setStatus(429);
            response.setContentType("application/json"); // Better to specify content type
            response.getWriter().write("{\"error\": \"Too many requests. Try again later.\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
