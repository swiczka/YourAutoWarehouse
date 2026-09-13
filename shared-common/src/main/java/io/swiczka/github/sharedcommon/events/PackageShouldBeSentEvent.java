package io.swiczka.github.sharedcommon.events;

public record PackageShouldBeSentEvent(
        Long packageId,
        Long layoutId,
        Long outboundOrderId,
        int currentX,
        int currentY,
        int targetX,
        int targetY
) {
}
