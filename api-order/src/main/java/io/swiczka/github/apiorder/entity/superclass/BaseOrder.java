package io.swiczka.github.apiorder.entity.superclass;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@MappedSuperclass
public abstract class BaseOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "layout_id")
    private Long layoutId;

    @Column(name = "operator_id")
    private UUID operatorId;

    @Column(name = "company_id")
    private Long companyId;

    @Column(name = "created_at")
    private Instant createdAt;

    public BaseOrder() {
    }

    public BaseOrder(Long layoutId, UUID operatorId, Long companyId, Instant createdAt) {
        this.layoutId = layoutId;
        this.operatorId = operatorId;
        this.companyId = companyId;
        this.createdAt = createdAt;
    }

    public Long getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(Long layoutId) {
        this.layoutId = layoutId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public UUID getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(UUID operatorId) {
        this.operatorId = operatorId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
