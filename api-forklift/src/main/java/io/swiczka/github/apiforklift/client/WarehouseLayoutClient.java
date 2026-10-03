package io.swiczka.github.apiforklift.client;

import io.swiczka.github.sharedcommon.dto.WarehouseLayoutDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Component
public class WarehouseLayoutClient {

    private static final Logger log = LoggerFactory.getLogger(WarehouseLayoutClient.class);
    private final RestClient restClient;

    public WarehouseLayoutClient(@Value("${warehouse.api.base-url}") final String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public Optional<WarehouseLayoutDto> getLayoutById(final Long layoutId) {
        if (layoutId == null) {
            return Optional.empty();
        }

        try {
            return Optional.ofNullable(
                    restClient.get()
                            .uri("/api/warehouse/layout/{id}/internal", layoutId)
                            .retrieve()
                            .onStatus(HttpStatusCode::isError, (request, response) -> {
                                log.warn("Failed to fetch layout with id={} from warehouse API, status={}",
                                        layoutId, response.getStatusCode());
                            })
                            .body(WarehouseLayoutDto.class)
            );
        } catch (final Exception e) {
            log.error("Error calling warehouse API for layoutId={}: {}", layoutId, e.getMessage());
            return Optional.empty();
        }
    }
}
