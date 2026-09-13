package io.swiczka.github.apiforklift.dto;

import io.swiczka.github.apiforklift.enums.TaskType;

public record ForkliftTaskCreateDto(
        Long packageItemId,
        Long layoutId,
        int sourceX,
        int sourceY,
        int targetX,
        int targetY,
        TaskType taskType
) {
    public ForkliftTaskCreateDto {
        if (taskType == null) {
            throw new IllegalArgumentException("TaskType cannot be null");
        }
    }
}
