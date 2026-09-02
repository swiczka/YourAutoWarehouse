package io.swiczka.github.apiorder.mapper;

import io.swiczka.github.apiorder.entity.InboundOrder;
import io.swiczka.github.apiorder.response.InboundOrderResponse;
import io.swiczka.github.sharedcommon.events.LayoutSavedEvent;
import io.swiczka.github.sharedcommon.events.OrderCreatedEvent;

public class InboundOrderMapper {
    private InboundOrderMapper() {
    }

    public static InboundOrderResponse toDto(final InboundOrder order){
        if(order == null) return null;

        return new InboundOrderResponse(
                order.getId(),
                order.getOperatorId(),
                order.getCreatedAt(),
                order.getCompanyId(),
                order.getStatus()
        );
    }

    public static OrderCreatedEvent toEvent(final InboundOrder entity) {
        if (entity == null) {
            return null;
        }

        return new OrderCreatedEvent(
                entity.getId(),
                entity.getOperatorId(),
                entity.getCompanyId()
        );
    }
}
