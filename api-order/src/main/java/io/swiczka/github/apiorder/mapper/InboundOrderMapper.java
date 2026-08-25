package io.swiczka.github.apiorder.mapper;

import io.swiczka.github.apiorder.entity.InboundOrder;
import io.swiczka.github.apiorder.response.InboundOrderResponse;

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
}
