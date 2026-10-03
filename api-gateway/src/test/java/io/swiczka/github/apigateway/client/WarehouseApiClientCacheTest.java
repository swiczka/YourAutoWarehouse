package io.swiczka.github.apigateway.client;

import io.swiczka.github.sharedcommon.dto.LayoutOwnerDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withResourceNotFound;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest(classes = WarehouseApiClientCacheTest.TestConfig.class)
class WarehouseApiClientCacheTest {

    @TestConfiguration
    @EnableCaching
    static class TestConfig {
        @Bean
        public CacheManager cacheManager() {
            return new ConcurrentMapCacheManager("layout_owners");
        }

        @Bean
        public RestClient.Builder restClientBuilder() {
            return RestClient.builder().baseUrl("http://localhost:8082");
        }

        @Bean
        public MockRestServiceServer mockRestServiceServer(final RestClient.Builder restClientBuilder) {
            return MockRestServiceServer.bindTo(restClientBuilder).build();
        }

        @Bean
        public WarehouseApiClient warehouseApiClient(final RestClient.Builder restClientBuilder,
                                                     final MockRestServiceServer mockRestServiceServer) {
            return new WarehouseApiClient(restClientBuilder.build());
        }
    }

    @Autowired
    private WarehouseApiClient warehouseApiClient;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        server.reset();
        final Cache cache = cacheManager.getCache("layout_owners");
        if (cache != null) {
            cache.clear();
        }
    }

    @Test
    @DisplayName("should cache layout owner on success and not hit network on second call")
    void shouldCacheLayoutOwner() {
        // given
        final Long layoutId = 22L;
        final String guestId = UUID.randomUUID().toString();
        final String jsonResponse = """
                {
                    "id": 22,
                    "userId": "%s"
                }
                """.formatted(guestId);

        // Server only expects 1 HTTP call
        server.expect(requestTo("http://localhost:8082/api/warehouse/layout/22/owner"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Guest-Id", guestId))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        // when - first call (cache miss)
        final Optional<LayoutOwnerDto> first = warehouseApiClient.getLayoutOwner(layoutId, guestId);

        // then
        assertThat(first).isPresent();
        assertThat(first.get().id()).isEqualTo(layoutId);

        // when - second call (should hit cache, no network call expected)
        final Optional<LayoutOwnerDto> second = warehouseApiClient.getLayoutOwner(layoutId, guestId);

        // then
        assertThat(second).isPresent();
        assertThat(second.get().id()).isEqualTo(layoutId);
        assertThat(second.get().userId()).isEqualTo(UUID.fromString(guestId));
        server.verify();
    }

    @Test
    @DisplayName("should not cache empty result when warehouse API returns 404")
    void shouldNotCacheEmptyResult() {
        // given
        final Long layoutId = 999L;
        final String guestId = UUID.randomUUID().toString();

        // Server expects two requests because 404 is not cached
        server.expect(requestTo("http://localhost:8082/api/warehouse/layout/999/owner"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withResourceNotFound());

        server.expect(requestTo("http://localhost:8082/api/warehouse/layout/999/owner"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withResourceNotFound());

        // when - first call
        final Optional<LayoutOwnerDto> first = warehouseApiClient.getLayoutOwner(layoutId, guestId);
        // when - second call
        final Optional<LayoutOwnerDto> second = warehouseApiClient.getLayoutOwner(layoutId, guestId);

        // then
        assertThat(first).isEmpty();
        assertThat(second).isEmpty();
        server.verify();
    }
}
