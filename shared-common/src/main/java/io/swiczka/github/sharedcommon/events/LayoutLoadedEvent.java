package io.swiczka.github.sharedcommon.events;

import java.util.List;
import java.util.UUID;

public record LayoutLoadedEvent(
        Long layoutId,
        UUID userId,
        Integer forkliftNumber,
        List<GridCellEvent> gridData
) {
}
