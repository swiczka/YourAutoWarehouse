package io.swiczka.github.apiforklift.domain;

import io.swiczka.github.apiforklift.enums.TaskStatus;

public class ForkliftTask {
    private final Long taskId;
    private final Long layoutId;
    private final Long packageId;
    private final int sourceX;
    private final int sourceY;
    private final int targetX;
    private final int targetY;
    private Long assignedForkliftId;
    private TaskStatus status;

    public ForkliftTask(Long taskId, Long layoutId, Long packageId, int sourceX, int sourceY, int targetX, int targetY) {
        this.taskId = taskId;
        this.layoutId = layoutId;
        this.packageId = packageId;
        this.sourceX = sourceX;
        this.sourceY = sourceY;
        this.targetX = targetX;
        this.targetY = targetY;
    }

    public Long getTaskId() {
        return taskId;
    }

    public Long getPackageId() {
        return packageId;
    }

    public Long getLayoutId() {
        return layoutId;
    }

    public int getSourceX() {
        return sourceX;
    }

    public int getSourceY() {
        return sourceY;
    }

    public int getTargetX() {
        return targetX;
    }

    public int getTargetY() {
        return targetY;
    }

    public Long getAssignedForkliftId() {
        return assignedForkliftId;
    }

    public void setAssignedForkliftId(Long assignedForkliftId) {
        this.assignedForkliftId = assignedForkliftId;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ForkliftTask{" +
                "taskId=" + taskId +
                ", layoutId=" + layoutId +
                ", packageId=" + packageId +
                ", sourceX=" + sourceX +
                ", sourceY=" + sourceY +
                ", targetX=" + targetX +
                ", targetY=" + targetY +
                ", assignedForkliftId=" + assignedForkliftId +
                ", status=" + status +
                '}';
    }
}
