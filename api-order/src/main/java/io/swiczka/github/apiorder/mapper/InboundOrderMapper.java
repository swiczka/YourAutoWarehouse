package io.swiczka.github.apiorder.mapper;

import io.swiczka.github.apiorder.dto.InboundOrderReadDto;
import io.swiczka.github.apiorder.entity.InboundOrder;

public class InboundOrderMapper {
    public static InboundOrderReadDto toDto(InboundOrder order){
        return new InboundOrderReadDto(
                order.getId(),
                order.getOperatorId(),
                order.getCreatedAt(),
                order.getCompanyId(),
                order.getStatus()
        );
    }
}
