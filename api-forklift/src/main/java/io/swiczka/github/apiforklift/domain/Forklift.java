package io.swiczka.github.apiforklift.domain;

import io.swiczka.github.apiforklift.enums.ForkliftStatus;
import io.swiczka.github.sharedcommon.helpers.Coordinate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Forklift {
    private Long id;
    private Long layoutId;
    private UUID operatorId;
    private ForkliftStatus status;
    private Long currentPackageId;
    private Long currentTaskId;
    private List<Coordinate> path = new ArrayList<>();
    private int x;
    private int y;

    public boolean isInGarage() {
        return this.status == ForkliftStatus.IN_GARAGE;
    }

    public boolean isFree() {
        return this.status == ForkliftStatus.IN_GARAGE ||
            this.status == ForkliftStatus.RETURNING;
    }

    public boolean hasPath() {
        return this.path != null && !this.path.isEmpty();
    }

    public void setPath(final List<Coordinate> newPath) {
        this.path = newPath != null ? new ArrayList<>(newPath) : new ArrayList<>();
    }

    public Optional<Coordinate> nextStep() {
        if (!hasPath()) {
            return Optional.empty();
        }
        final Coordinate next = this.path.removeFirst();
        this.x = next.x();
        this.y = next.y();
        return Optional.of(next);
    }

    public Long getCurrentTaskId() {
        return currentTaskId;
    }

    public void setCurrentTaskId(final Long currentTaskId) {
        this.currentTaskId = currentTaskId;
    }

    public List<Coordinate> getPath() {
        return path;
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

    public Long getCurrentPackageId() {
        return currentPackageId;
    }

    public void setCurrentPackageId(Long currentPackageId) {
        this.currentPackageId = currentPackageId;
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
