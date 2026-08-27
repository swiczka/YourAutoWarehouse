package io.swiczka.github.apiwarehouse.layout.helpers;

import io.swiczka.github.sharedcommon.helpers.Coordinate;
import io.swiczka.github.sharedcommon.helpers.Direction;

import java.util.Set;

public record RoadCell(
        Coordinate coordinate,
        Set<Direction> allowedDirections,
        Boolean isShelf,
        Boolean isRoad
){}
