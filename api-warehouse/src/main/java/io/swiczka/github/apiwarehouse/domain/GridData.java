package io.swiczka.github.apiwarehouse.domain;

import io.swiczka.github.sharedcommon.helpers.Coordinate;
import io.swiczka.github.apiwarehouse.layout.helpers.Direction;

import java.util.Set;

public record GridData(
    Set<Direction> allowedDirections,
    Coordinate coordinates,
    Boolean isShelf,
    Boolean isRoad
) {

}
