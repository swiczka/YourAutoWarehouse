package io.swiczka.github.sharedcommon.dto;

import io.swiczka.github.sharedcommon.events.GridCellEvent;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record WarehouseLayoutDto(
        Long id,
        UUID userId,
        Instant createdAt,
        Integer forkliftNumber,
        List<GridCellEvent> gridData
) {}
