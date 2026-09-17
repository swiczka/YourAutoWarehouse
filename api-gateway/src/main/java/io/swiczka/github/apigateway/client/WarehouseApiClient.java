package io.swiczka.github.apigateway.client;

import io.swiczka.github.sharedcommon.dto.LayoutOwnerDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Component
public class WarehouseApiClient {

    private static final Logger log = LoggerFactory.getLogger(WarehouseApiClient.class);
    private static final String GUEST_ID_HEADER = "X-Guest-Id";

    private final RestClient restClient;

    public WarehouseApiClient(@Value("${warehouse.api.base-url}") final String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public Optional<LayoutOwnerDto> getLayoutOwner(final Long layoutId, final String guestId) {
        try {
            return Optional.ofNullable(
                    restClient.get()
                            .uri("/api/warehouse/layout/{id}/owner", layoutId)
                            .header(GUEST_ID_HEADER, guestId)
                            .retrieve()
                            .onStatus(HttpStatusCode::isError, (request, response) -> {
                                log.warn("Ownership check failed for layoutId={}, guestId={}, status={}",
                                        layoutId, guestId, response.getStatusCode());
                            })
                            .body(LayoutOwnerDto.class)
            );
        } catch (final Exception e) {
            log.error("Failed to verify layout ownership for layoutId={}, guestId={}: {}",
                    layoutId, guestId, e.getMessage());
            return Optional.empty();
        }
    }
}
