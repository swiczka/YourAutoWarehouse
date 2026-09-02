package io.swiczka.github.sharedcommon.events;

import java.time.Instant;

public record ForkliftLocationEvent(
        Long forkliftId,
        Long layoutId,
        int x,
        int y,
        Instant timestamp
) {}
