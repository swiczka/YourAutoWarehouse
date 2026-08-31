package io.swiczka.github.sharedcommon.events;

import java.util.List;
import java.util.UUID;

public record LayoutSavedEvent(
        Long layoutId,
        UUID userId,
        Integer forkliftNumber,
        List<GridCellEvent> gridData
) {
}
