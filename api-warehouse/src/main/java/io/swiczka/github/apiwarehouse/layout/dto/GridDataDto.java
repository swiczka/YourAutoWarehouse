package io.swiczka.github.apiwarehouse.layout.dto;

import io.swiczka.github.apiwarehouse.layout.helpers.Direction;
import java.util.List;
import io.swiczka.github.sharedcommon.helpers.Coordinate;

public record GridDataDto(
        List<Direction> allowedDirections,
        Coordinate coordinates,
        Boolean isShelf,
        Boolean isRoad
) {
}
