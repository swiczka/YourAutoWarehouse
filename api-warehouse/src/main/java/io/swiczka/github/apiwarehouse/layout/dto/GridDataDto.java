package io.swiczka.github.apiwarehouse.layout.dto;

import io.swiczka.github.apiwarehouse.domain.GridData;
import io.swiczka.github.apiwarehouse.layout.helpers.Coordinate;
import io.swiczka.github.apiwarehouse.layout.helpers.Direction;
import java.util.List;

public record GridDataDto(
        List<Direction> allowedDirections,
        Coordinate coordinates,
        Boolean isShelf,
        Boolean isRoad
) {
}
