package io.swiczka.github.sharedcommon.events;

public record PackageAllocatedEvent(
    Long packageId,
    Long layoutId,
    String name,
    int x,
    int y)
    {}
