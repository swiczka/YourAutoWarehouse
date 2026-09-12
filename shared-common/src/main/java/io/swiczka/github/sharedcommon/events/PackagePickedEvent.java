package io.swiczka.github.sharedcommon.events;

public record PackagePickedEvent(
        Long packageId,
        Long forkliftId,
        Long layoutId
) {}



