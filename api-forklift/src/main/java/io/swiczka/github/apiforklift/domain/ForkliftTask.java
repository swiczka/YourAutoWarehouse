package io.swiczka.github.apiforklift.domain;

import io.swiczka.github.apiforklift.enums.TaskStatus;
import io.swiczka.github.sharedcommon.helpers.Coordinate;

public class ForkliftTask {
    private final Long taskId;
    private final Long packageId;
    private final int sourceX;
    private final int sourceY;
    private final int targetX;
    private final int targetY;
    private Long assignedForkliftId;
    private TaskStatus status;

    public ForkliftTask(Long taskId, Long packageId, int sourceX, int sourceY, int targetX, int targetY) {
        this.taskId = taskId;
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
}
