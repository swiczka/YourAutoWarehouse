package io.swiczka.github.apiforklift.domain;

import io.swiczka.github.apiforklift.enums.ForkliftStatus;

import java.util.UUID;

public class Forklift {
    private Long id;
    private Long layoutId;
    private UUID operatorId;
    private ForkliftStatus status;
    private int x;
    private int y;

    public boolean isInGarage() {
        return this.status == ForkliftStatus.IN_GARAGE;
    }

    public Forklift() {
    }

    public Forklift(Long id, Long layoutId, UUID operatorId, ForkliftStatus status, int x, int y) {
        this.id = id;
        this.layoutId = layoutId;
        this.operatorId = operatorId;
        this.status = status;
        this.x = x;
        this.y = y;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(Long layoutId) {
        this.layoutId = layoutId;
    }

    public UUID getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(UUID operatorId) {
        this.operatorId = operatorId;
    }

    public ForkliftStatus getStatus() {
        return status;
    }

    public void setStatus(ForkliftStatus status) {
        this.status = status;
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

    @Override
    public String toString() {
        return "Forklift{" +
                "id=" + id +
                ", layoutId=" + layoutId +
                ", operatorId=" + operatorId +
                ", status=" + status +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
