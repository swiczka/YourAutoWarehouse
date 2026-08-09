package io.swiczka.github.apiwarehouse.layout.helpers;

import java.util.Set;

public record RoadCell(
        Coordinate coordinate,
        Set<Direction> allowedDirections,
        Boolean isShelf,
        Boolean isRoad
){}
