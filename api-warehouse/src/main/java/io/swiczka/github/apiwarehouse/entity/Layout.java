package io.swiczka.github.apiwarehouse.entity;

import io.swiczka.github.apiwarehouse.domain.GridData;
import io.swiczka.github.apiwarehouse.layout.dto.GridDataDto;
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
    private long id;

    @Column(name="user_id")
    private UUID userId;

    @Column(name="created_at")
    private Instant createdAt;

    @Column(name = "grid_data", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<GridData> gridData;

    public Layout() {
    }

    public Layout(UUID userId, Instant createdAt, List<GridData> gridData) {
        this.userId = userId;
        this.createdAt = createdAt;
        this.gridData = gridData;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
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
