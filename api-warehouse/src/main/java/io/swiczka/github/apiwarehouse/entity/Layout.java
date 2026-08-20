package io.swiczka.github.apiwarehouse.entity;

import io.swiczka.github.apiwarehouse.domain.GridData;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="Layouts")
public class Layout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="user_id")
    private UUID userId;

    @Column(name="created_at")
    private Instant createdAt;

    @Column(name="forklift_number")
    private Integer forkliftNumber;

    @Column(name = "grid_data", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<GridData> gridData;

    public Layout() {
    }

    public Layout(UUID userId, Instant createdAt, Integer forkliftNumber, List<GridData> gridData) {
        this.userId = userId;
        this.createdAt = createdAt;
        this.forkliftNumber = forkliftNumber;
        this.gridData = gridData;
    }

    public Integer getForkliftNumber() {
        return forkliftNumber;
    }

    public void setForkliftNumber(Integer forkliftNumber) {
        this.forkliftNumber = forkliftNumber;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public List<GridData> getGridData() {
        return gridData;
    }

    public void setGridData(List<GridData> gridData) {
        this.gridData = gridData;
    }
}
