package io.swiczka.github.sharedcommon.events;

public record PackageDroppedEvent(
        Long packageId,
        Long layoutId,
        int x,
        int y
) {
}
