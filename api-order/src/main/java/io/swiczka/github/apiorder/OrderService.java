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
import io.swiczka.github.apiorder.producer.OrderEventProducer;
import io.swiczka.github.apiorder.response.InboundOrderResponse;
import io.swiczka.github.apiorder.response.OutboundOrderResponse;
import io.swiczka.github.sharedcommon.helpers.TaskType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional
@Service
public class OrderService {

    private final InboundOrderDAO inboundDAO;
    private final OutboundOrderDAO outboundDAO;
    private final OrderEventProducer orderEventProducer;

    @Autowired
    public OrderService(final InboundOrderDAO inboundDAO, final OutboundOrderDAO outboundDAO, final OrderEventProducer orderEventProducer) {
        this.inboundDAO = inboundDAO;
        this.outboundDAO = outboundDAO;
        this.orderEventProducer = orderEventProducer;
    }

    public InboundOrderResponse addInboundOrder(final InboundOrderCreateDto dto, final UUID guestId) {
        final InboundOrder newOrder = new InboundOrder(
                dto.layoutId(),
                guestId,
                dto.companyId(),
                Instant.now(),
                InboundOrderStatus.PENDING
        );

        this.inboundDAO.save(newOrder);

        orderEventProducer.sendInboundOrderCreated(InboundOrderMapper.toEvent(newOrder, dto.packageNames()));

        return InboundOrderMapper.toDto(newOrder);
    }

    public List<InboundOrderResponse> getInboundByUser(final UUID userId) {
        final List<InboundOrder> orders = this.inboundDAO.getInboundByUser(userId);

        return orders.stream()
                .map(InboundOrderMapper::toDto)
                .toList();
    }


    public OutboundOrderResponse addOutboundOrder(final OutboundOrderCreateDto dto, final UUID guestId) {
        final OutboundOrder newOrder = new OutboundOrder(
                dto.layoutId(),
                guestId,
                dto.companyId(),
                Instant.now(),
                OutboundOrderStatus.PENDING
        );

        this.outboundDAO.save(newOrder);

        this.orderEventProducer.sendOutboundOrderCreated(OutboundOrderMapper.toEvent(newOrder, dto.packageIds()));

        return OutboundOrderMapper.toDto(newOrder);
    }

    public List<OutboundOrderResponse> getOutboundByUser(final UUID userId) {
        final List<OutboundOrder> orders = this.outboundDAO.getOutboundByUser(userId);

        return orders.stream()
                .map(OutboundOrderMapper::toDto)
                .toList();
    }

    public List<OutboundOrderResponse> getOutboundByLayout(final UUID userId, final Long layoutId) {
        final List<OutboundOrder> orders = this.outboundDAO.getOutboundByLayoutAndUser(layoutId, userId);
        return orders.stream()
                .map(OutboundOrderMapper::toDto)
                .toList();
    }

    public List<InboundOrderResponse> getInboundByLayout(final UUID userId, final Long layoutId) {
        final List<InboundOrder> orders = this.inboundDAO.getInboundByLayoutAndUser(layoutId, userId);
        return orders.stream()
                .map(InboundOrderMapper::toDto)
                .toList();
    }

    public void handleOrderComplete(final Long orderId, final TaskType taskType){
        if(taskType == TaskType.INBOUND){
            Optional<InboundOrder> orderOpt = inboundDAO.getInboundById(orderId);
            if(orderOpt.isEmpty()) return;
            InboundOrder order = orderOpt.get();
            order.setStatus(InboundOrderStatus.COMPLETED);
            inboundDAO.save(order);
        }
        else if(taskType == TaskType.OUTBOUND){
            Optional<OutboundOrder> orderOpt = outboundDAO.getOutboundById(orderId);
            if(orderOpt.isEmpty()) return;
            OutboundOrder order = orderOpt.get();
            order.setStatus(OutboundOrderStatus.COMPLETED);
            outboundDAO.save(order);
        }
    }
}
