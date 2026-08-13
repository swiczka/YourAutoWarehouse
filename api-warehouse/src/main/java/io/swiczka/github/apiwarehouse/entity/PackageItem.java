package io.swiczka.github.apiwarehouse.entity;

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
    private int x;

    @Column(name = "y")
    private int y;

    public PackageItem() {
    }

    public PackageItem(String name, Long inboundOrderId, Long outboundOrderId, int x, int y) {
        this.name = name;
        this.inboundOrderId = inboundOrderId;
        this.outboundOrderId = outboundOrderId;
        this.x = x;
        this.y = y;
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
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
