package io.swiczka.github.apiorder.mapper;

import io.swiczka.github.apiorder.entity.OutboundOrder;
import io.swiczka.github.apiorder.response.OutboundOrderResponse;
import io.swiczka.github.sharedcommon.events.OutboundOrderCreatedEvent;

import java.util.List;

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

    public static OutboundOrderCreatedEvent toEvent(final OutboundOrder entity, final List<Long> packageIds) {
        if (entity == null) {
            return null;
        }

        return new OutboundOrderCreatedEvent(
                entity.getId(),
                entity.getOperatorId(),
                entity.getCompanyId(),
                entity.getLayoutId(),
                packageIds
        );
    }
}
