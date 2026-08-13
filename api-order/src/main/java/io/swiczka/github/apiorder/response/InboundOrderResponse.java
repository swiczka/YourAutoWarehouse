package io.swiczka.github.apiorder.response;

import java.util.UUID;

public record InboundOrderResponse(
        Long id,

        UUID operatorId, //related to browser userId

        Long companyId, //company that owns packages

        int packageCount
) {}
