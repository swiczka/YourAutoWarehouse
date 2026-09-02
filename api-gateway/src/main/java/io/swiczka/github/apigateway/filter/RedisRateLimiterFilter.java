package io.swiczka.github.apigateway.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

@Component
public class RedisRateLimiterFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RedisRateLimiterFilter.class);

    private static final String GUEST_ID_HEADER = "X-Guest-Id";
    private static final String REDIS_KEY_PREFIX = "rate_limit:";
    private static final long MAX_REQUESTS_PER_10_SECONDS = 30L;
    private static final Duration KEY_TTL = Duration.ofSeconds(13);
    private static final String WEBSOCKET_PATH = "/ws";

    private final StringRedisTemplate redisTemplate;

    public RedisRateLimiterFilter(final StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected boolean shouldNotFilter(final HttpServletRequest request) {
        final String path = request.getRequestURI();
        return path != null && path.startsWith(WEBSOCKET_PATH);
    }

    @Override
    protected void doFilterInternal(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain filterChain
    ) throws ServletException, IOException {
        final String clientIdentifier = resolveClientIdentifier(request);
        final long currentTenSecond = Instant.now().getEpochSecond() / 10;
        final String redisKey = REDIS_KEY_PREFIX + clientIdentifier + ":" + currentTenSecond;

        try {
            final Long requestCount = redisTemplate.opsForValue().increment(redisKey);

            if (requestCount != null && requestCount == 1L) {
                redisTemplate.expire(redisKey, KEY_TTL);
            }

            if (requestCount != null && requestCount > MAX_REQUESTS_PER_10_SECONDS) {
                log.warn("Rate limit exceeded for client: {}. Requests in current second: {}", clientIdentifier, requestCount);
                rejectWithTooManyRequests(response);
                return;
            }
        } catch (final Exception e) {
            log.error("Error communicating with Redis for rate limiting, allowing request through: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private String resolveClientIdentifier(final HttpServletRequest request) {
        final String guestId = request.getHeader(GUEST_ID_HEADER);
        if (guestId != null && !guestId.isBlank()) {
            return guestId.trim();
        }
        return request.getRemoteAddr();
    }

    private void rejectWithTooManyRequests(final HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("""
                {
                    "status": 429,
                    "error": "Too Many Requests",
                    "message": "Rate limit exceeded. Maximum 10 requests per second."
                }
                """);
    }
}
