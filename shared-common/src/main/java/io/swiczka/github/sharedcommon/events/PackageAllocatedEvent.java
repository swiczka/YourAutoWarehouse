package io.swiczka.github.sharedcommon.events;

public record PackageAllocatedEvent(
        Long packageId,
        Long layoutId,
        Long inboundOrderId,
        String name,
        String status,
        int currentX,
        int currentY,
        int targetX,
        int targetY
) {}
