package io.swiczka.github.apigateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimiterConfig {
    @Bean
    @Primary
    public KeyResolver userKeyResolver() {
        return exchange -> {
            final String guestSessionId = exchange.getRequest().getHeaders().getFirst("X-Guest-Id");
            if (guestSessionId != null && !guestSessionId.isBlank()) {
                return Mono.just(guestSessionId);
            }
            return Mono.just(exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
        };
    }

}
