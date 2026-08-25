package io.swiczka.github.apiorder;

import io.swiczka.github.apiorder.dao.InboundOrderDAO;
import io.swiczka.github.apiorder.dao.OutboundOrderDAO;
import io.swiczka.github.apiorder.dto.InboundOrderCreateDto;
import io.swiczka.github.apiorder.dto.OutboundOrderCreateDto;
import io.swiczka.github.apiorder.entity.InboundOrder;
import io.swiczka.github.apiorder.entity.OutboundOrder;
import io.swiczka.github.apiorder.enums.InboundOrderStatus;
import io.swiczka.github.apiorder.enums.OutboundOrderStatus;
import io.swiczka.github.apiorder.mapper.InboundOrderMapper;
import io.swiczka.github.apiorder.mapper.OutboundOrderMapper;
import io.swiczka.github.apiorder.response.InboundOrderResponse;
import io.swiczka.github.apiorder.response.OutboundOrderResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final InboundOrderDAO inboundDAO;
    private final OutboundOrderDAO outboundDAO;

    @Autowired
    public OrderService(final InboundOrderDAO inboundDAO, final OutboundOrderDAO outboundDAO) {
        this.inboundDAO = inboundDAO;
        this.outboundDAO = outboundDAO;
    }

    @Transactional
    public InboundOrderResponse addInboundOrder(final InboundOrderCreateDto dto, final UUID guestId) {
        final InboundOrder newOrder = new InboundOrder(
                guestId,
                dto.companyId(),
                Instant.now(),
                InboundOrderStatus.PENDING
        );

        this.inboundDAO.save(newOrder);
        return InboundOrderMapper.toDto(newOrder);
    }

    public List<InboundOrderResponse> getInboundByUser(final UUID userId) {
        final List<InboundOrder> orders = this.inboundDAO.getInboundByUser(userId);

        return orders.stream()
                .map(InboundOrderMapper::toDto)
                .toList();
    }

    @Transactional
    public OutboundOrderResponse addOutboundOrder(final OutboundOrderCreateDto dto, final UUID guestId) {
        final OutboundOrder newOrder = new OutboundOrder(
                guestId,
                dto.companyId(),
                Instant.now(),
                OutboundOrderStatus.PENDING
        );

        this.outboundDAO.save(newOrder);
        return OutboundOrderMapper.toDto(newOrder);
    }

    public List<OutboundOrderResponse> getOutboundByUser(final UUID userId) {
        final List<OutboundOrder> orders = this.outboundDAO.getOutboundByUser(userId);

        return orders.stream()
                .map(OutboundOrderMapper::toDto)
                .toList();
    }
}
