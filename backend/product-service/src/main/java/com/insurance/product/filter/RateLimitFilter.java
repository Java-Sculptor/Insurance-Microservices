
package com.insurance.product.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS = 100;
    private static final long WINDOW_MS = 60_000L;

    private final ConcurrentHashMap<String, RequestCounter> counters =
            new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String clientIp = request.getRemoteAddr();
        long currentTime = System.currentTimeMillis();

        RequestCounter counter = counters.compute(
                clientIp,
                (ip, existing) -> {
                    if (existing == null
                            || currentTime - existing.windowStart >= WINDOW_MS) {
                        return new RequestCounter(currentTime, 1);
                    }

                    existing.count++;
                    return existing;
                }
        );

        response.setHeader(
                "X-RateLimit-Limit",
                String.valueOf(MAX_REQUESTS)
        );

        if (counter.count > MAX_REQUESTS) {

            long elapsed = currentTime - counter.windowStart;
            long remainingMs = Math.max(1L, WINDOW_MS - elapsed);
            long retryAfterSeconds = (remainingMs + 999) / 1000;

            response.setHeader(
                    "Retry-After",
                    String.valueOf(retryAfterSeconds)
            );

            response.setStatus(429);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            response.getWriter().write(
                    "{\"success\":false,"
                            + "\"message\":\"Rate limit exceeded. Try again later.\"}"
            );

            return;
        }

        filterChain.doFilter(request, response);
    }

    private static class RequestCounter {

        private final long windowStart;
        private int count;

        private RequestCounter(long windowStart, int count) {
            this.windowStart = windowStart;
            this.count = count;
        }
    }
}
