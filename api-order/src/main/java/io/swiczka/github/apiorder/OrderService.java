package io.swiczka.github.apiorder;

import io.swiczka.github.apiorder.dao.InboundOrderDAO;
import io.swiczka.github.apiorder.dao.OutboundOrderDAO;
import io.swiczka.github.apiorder.dto.InboundOrderCreateDto;
import io.swiczka.github.apiorder.dto.InboundOrderReadDto;
import io.swiczka.github.apiorder.entity.InboundOrder;
import io.swiczka.github.apiorder.enums.InboundOrderStatus;
import io.swiczka.github.apiorder.mapper.InboundOrderMapper;
import io.swiczka.github.apiorder.response.InboundOrderResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {
    private final InboundOrderDAO inboundDAO;
    private final OutboundOrderDAO outboundDAO;

    public OrderService(InboundOrderDAO inboundDAO, OutboundOrderDAO outboundDAO) {
        this.inboundDAO = inboundDAO;
        this.outboundDAO = outboundDAO;
    }

    @Transactional
    public InboundOrderResponse addInboundOrder(InboundOrderCreateDto dto){
        InboundOrder newOrder = new InboundOrder(dto.operatorId(),
                dto.companyId(),
                Instant.now(),
                InboundOrderStatus.PENDING);

        this.inboundDAO.save(newOrder);
        return new InboundOrderResponse(
                newOrder.getId(),
                newOrder.getOperatorId(),
                newOrder.getCompanyId(),
                dto.packageNames().size()
        );
    }

    public List<InboundOrderReadDto> getInboundByUser(UUID userId) {
        List<InboundOrder> orders = this.inboundDAO.getInboundByUser(userId);

        return orders.stream()
                .map(InboundOrderMapper::toDto)
                .toList();
    }
}
