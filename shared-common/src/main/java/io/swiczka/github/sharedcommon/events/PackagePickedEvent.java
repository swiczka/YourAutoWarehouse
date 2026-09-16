package io.swiczka.github.sharedcommon.events;

import io.swiczka.github.sharedcommon.helpers.TaskType;

public record PackagePickedEvent(
        Long packageId,
        Long forkliftId,
        Long layoutId,
        TaskType taskType
) {}



