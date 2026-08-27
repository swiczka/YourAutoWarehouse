package io.swiczka.github.apiforklift.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swiczka.github.sharedcommon.helpers.Coordinate;
import io.swiczka.github.sharedcommon.helpers.Direction;

import java.util.Set;

public record SimulationGridData(
        Coordinate coordinates,
        Set<Direction> allowedDirections,
        @JsonProperty("isRoad") Boolean isRoad,
        @JsonProperty("isShelf") Boolean isShelf
) {
}
