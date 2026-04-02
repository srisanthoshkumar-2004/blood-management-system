package com.bms.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    // Max 10,000 tracked IPs to prevent OOM under DDoS
    private static final int MAX_BUCKET_ENTRIES = 10_000;
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        
        // Strict limiting for Auth and Emergency endpoints
        if (path.startsWith("/api/auth/") || path.startsWith("/api/request/")) {
            String ip = request.getRemoteAddr();
            
            // Evict all entries if map exceeds safety threshold (anti-OOM protection)
            if (buckets.size() > MAX_BUCKET_ENTRIES) {
                buckets.clear();
            }
            
            Bucket bucket = buckets.computeIfAbsent(ip, this::createNewBucket);
            
            if (bucket.tryConsume(1)) {
                filterChain.doFilter(request, response);
            } else {
                response.setStatus(429); // TOO_MANY_REQUESTS
                response.setContentType("text/plain");
                response.getWriter().write("Security: Rate limit exceeded. Please try again after 60 seconds.");
                return;
            }
            return;
        }
        
        filterChain.doFilter(request, response);
    }

    private Bucket createNewBucket(String key) {
        // 20 requests per minute - standard threshold for emergency lookups
        Refill refill = Refill.intervally(20, Duration.ofMinutes(1));
        Bandwidth limit = Bandwidth.classic(20, refill);
        return Bucket.builder().addLimit(limit).build();
    }
}
