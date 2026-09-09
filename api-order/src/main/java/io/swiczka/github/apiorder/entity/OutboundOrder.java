package io.swiczka.github.apiorder.entity;

import io.swiczka.github.apiorder.entity.superclass.BaseOrder;
import io.swiczka.github.apiorder.enums.OutboundOrderStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbound_orders")
public class OutboundOrder extends BaseOrder {

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private OutboundOrderStatus status;

    public OutboundOrder() {
    }

    public OutboundOrder(
            final Long layoutId,
            final UUID operatorId,
            final Long companyId,
            final Instant createdAt,
            final OutboundOrderStatus status
    ) {
        super(layoutId, operatorId, companyId, createdAt);
        this.status = status;
    }

    public OutboundOrderStatus getStatus() {
        return status;
    }

    public void setStatus(OutboundOrderStatus status) {
        this.status = status;
    }
}
