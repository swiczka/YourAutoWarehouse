package io.swiczka.github.sharedcommon.events;

public record PackageStoredEvent (
        Long packageId,
        Long layoutId,
        int x,
        int y
)
{}
