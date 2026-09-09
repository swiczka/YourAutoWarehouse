package io.swiczka.github.apiorder.response;

import io.swiczka.github.apiorder.enums.InboundOrderStatus;

import java.time.Instant;
import java.util.UUID;

public record InboundOrderResponse(
        Long id,

        Long layoutId,

        UUID operatorId, //related to browser userId

        Instant createdAt,

        Long companyId, //company that owns packages

        InboundOrderStatus status
) {}
