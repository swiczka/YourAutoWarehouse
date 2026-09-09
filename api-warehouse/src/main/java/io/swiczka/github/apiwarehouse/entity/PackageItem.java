package io.swiczka.github.apiwarehouse.entity;

import io.swiczka.github.apiwarehouse.enums.PackageStatus;
import jakarta.persistence.*;

@Entity
@Table(name="Packages")
public class PackageItem {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "inbound_order_id")
    private Long inboundOrderId;

    @Column(name = "outbound_order_id")
    private Long outboundOrderId;

    @Column(name = "x")
    private Integer x;

    @Column(name = "y")
    private Integer y;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private PackageStatus status;

    @Column(name = "layout_id")
    private Long layoutId;

    public PackageItem() {
    }

    public PackageItem(
            final Long layoutId,
            final String name,
            final Long inboundOrderId,
            final Long outboundOrderId,
            final Integer x,
            final Integer y,
            final PackageStatus status
    ) {
        this.layoutId = layoutId;
        this.name = name;
        this.inboundOrderId = inboundOrderId;
        this.outboundOrderId = outboundOrderId;
        this.x = x;
        this.y = y;
        this.status = status;
    }

    public PackageItem(
            final Long layoutId,
            final String name,
            final Long inboundOrderId,
            final PackageStatus status
    ) {
        this(layoutId, name, inboundOrderId, null, null, null, status);
    }

    public PackageItem(
            final String name,
            final Long inboundOrderId,
            final Long outboundOrderId,
            final Integer x,
            final Integer y,
            final PackageStatus status
    ) {
        this(null, name, inboundOrderId, outboundOrderId, x, y, status);
    }

    public PackageItem(
            final String name,
            final Long inboundOrderId,
            final Long outboundOrderId,
            final Integer x,
            final Integer y
    ) {
        this(null, name, inboundOrderId, outboundOrderId, x, y, null);
    }

    public PackageItem(
            final String name,
            final Long inboundOrderId,
            final PackageStatus status
    ) {
        this(null, name, inboundOrderId, null, null, null, status);
    }

    public Long getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(final Long layoutId) {
        this.layoutId = layoutId;
    }

    public PackageStatus getStatus() {
        return status;
    }

    public void setStatus(final PackageStatus status) {
        this.status = status;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getInboundOrderId() {
        return inboundOrderId;
    }

    public void setInboundOrderId(Long inboundOrderId) {
        this.inboundOrderId = inboundOrderId;
    }

    public Long getOutboundOrderId() {
        return outboundOrderId;
    }

    public void setOutboundOrderId(Long outboundOrderId) {
        this.outboundOrderId = outboundOrderId;
    }
    public Integer getX() {
        return x;
    }

    public void setX(final Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(final Integer y) {
        this.y = y;
    }
}
