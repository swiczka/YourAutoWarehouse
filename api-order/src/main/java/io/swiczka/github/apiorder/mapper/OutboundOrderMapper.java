package io.swiczka.github.apiorder.mapper;

import io.swiczka.github.apiorder.entity.OutboundOrder;
import io.swiczka.github.apiorder.response.OutboundOrderResponse;

public final class OutboundOrderMapper {

    private OutboundOrderMapper() {
        // Utility class
    }

    public static OutboundOrderResponse toDto(final OutboundOrder order) {
        if (order == null) {
            return null;
        }

        return new OutboundOrderResponse(
                order.getId(),
                order.getLayoutId(),
                order.getOperatorId(),
                order.getCreatedAt(),
                order.getCompanyId(),
                order.getStatus()
        );
    }
}
