package io.swiczka.github.apiorder.dto;

import io.swiczka.github.apiorder.enums.InboundOrderStatus;

import java.time.Instant;
import java.util.UUID;

public record InboundOrderReadDto(
        Long id,
        UUID operatorId,
        Instant createdAt,
        Long companyId,
        InboundOrderStatus status
) {
}
