package io.swiczka.github.apiorder.mapper;

import io.swiczka.github.apiorder.entity.InboundOrder;
import io.swiczka.github.apiorder.response.InboundOrderResponse;
import io.swiczka.github.sharedcommon.events.InboundOrderCreatedEvent;

import java.util.List;

public class InboundOrderMapper {
    private InboundOrderMapper() {
    }

    public static InboundOrderResponse toDto(final InboundOrder order){
        if(order == null) return null;

        return new InboundOrderResponse(
                order.getId(),
                order.getLayoutId(),
                order.getOperatorId(),
                order.getCreatedAt(),
                order.getCompanyId(),
                order.getStatus()
        );
    }

    public static InboundOrderCreatedEvent toEvent(final InboundOrder entity, final List<String> packageNames) {
        if (entity == null) {
            return null;
        }

        return new InboundOrderCreatedEvent(
                entity.getId(),
                entity.getOperatorId(),
                entity.getCompanyId(),
                entity.getLayoutId(),
                packageNames
        );
    }
}
