package io.swiczka.github.apiwarehouse.domain;

import io.swiczka.github.sharedcommon.helpers.Coordinate;
import io.swiczka.github.apiwarehouse.layout.helpers.Direction;

import java.util.List;

public record GridData(
    List<Direction> allowedDirections,
    Coordinate coordinates,
    Boolean isShelf,
    Boolean isRoad
) {

}
