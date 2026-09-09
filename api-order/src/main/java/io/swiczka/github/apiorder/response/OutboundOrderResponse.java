package io.swiczka.github.apiorder.response;

import io.swiczka.github.apiorder.enums.OutboundOrderStatus;

import java.time.Instant;
import java.util.UUID;

public record OutboundOrderResponse(
        Long id,
        Long layoutId,
        UUID operatorId,
        Instant createdAt,
        Long companyId,
        OutboundOrderStatus status
) {
}
