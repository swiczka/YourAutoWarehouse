package io.swiczka.github.sharedcommon.events;

public record LayoutForkliftNumberChangedEvent(
        Long layoutId,
        Integer forkliftNumber
) {
}
