package io.swiczka.github.apigateway.client;

import io.swiczka.github.sharedcommon.dto.LayoutOwnerDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Component
public class WarehouseApiClient {

    private static final Logger log = LoggerFactory.getLogger(WarehouseApiClient.class);
    private static final String GUEST_ID_HEADER = "X-Guest-Id";

    private final RestClient restClient;

    @Autowired
    public WarehouseApiClient(@Value("${warehouse.api.base-url}") final String baseUrl) {
        this(RestClient.builder().baseUrl(baseUrl).build());
    }

    WarehouseApiClient(final RestClient restClient) {
        this.restClient = restClient;
    }

    @Cacheable(
            value = "layout_owners",
            key = "#layoutId",
            unless = "#result == null"
    )
    public Optional<LayoutOwnerDto> getLayoutOwner(final Long layoutId, final String guestId) {
        log.info("Cache miss: querying warehouse API for layoutId={}", layoutId);
        try {
            final LayoutOwnerDto dto = restClient.get()
                    .uri("/api/warehouse/layout/{id}/owner", layoutId)
                    .header(GUEST_ID_HEADER, guestId)
                    .retrieve()
                    .body(LayoutOwnerDto.class);

            if (dto == null || dto.userId() == null) {
                return Optional.empty();
            }
            return Optional.of(dto);
        } catch (final HttpClientErrorException.NotFound e) {
            log.info("Layout owner not found for layoutId={}", layoutId);
            return Optional.empty();
        } catch (final Exception e) {
            log.error("Failed to verify layout ownership for layoutId={}, guestId={}: {}",
                    layoutId, guestId, e.getMessage());
            return Optional.empty();
        }
    }
}
