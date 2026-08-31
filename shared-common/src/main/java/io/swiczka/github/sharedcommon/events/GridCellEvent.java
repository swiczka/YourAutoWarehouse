package io.swiczka.github.sharedcommon.events;

import io.swiczka.github.sharedcommon.helpers.Coordinate;
import io.swiczka.github.sharedcommon.helpers.Direction;

import java.util.Set;

public record GridCellEvent(
        Set<Direction> allowedDirections,
        Coordinate coordinates,

        Boolean isRoad,
        Boolean isShelf
) {
}
