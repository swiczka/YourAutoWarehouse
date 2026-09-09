package io.swiczka.github.apiorder.entity;

import io.swiczka.github.apiorder.entity.superclass.BaseOrder;
import io.swiczka.github.apiorder.enums.InboundOrderStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "inbound_orders")
public class InboundOrder extends BaseOrder {

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private InboundOrderStatus status;

    public InboundOrder() {
    }

    public InboundOrder(
            final Long layoutId,
            final UUID operatorId,
            final Long companyId,
            final Instant createdAt,
            final InboundOrderStatus status
    ) {
        super(layoutId, operatorId, companyId, createdAt);
        this.status = status;
    }

    public InboundOrderStatus getStatus() {
        return status;
    }

    public void setStatus(InboundOrderStatus status) {
        this.status = status;
    }
}