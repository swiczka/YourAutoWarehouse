package io.swiczka.github.apiwarehouse.layout.dto;

import io.swiczka.github.apiwarehouse.layout.helpers.Direction;
import java.util.Set;

import io.swiczka.github.sharedcommon.helpers.Coordinate;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.UniqueElements;

public record GridDataDto(
        @Size(min = 0, max = 4)
        @UniqueElements(message = "Directions must be unique")
        Set<@NotNull(message = "Direction cannot be null") Direction> allowedDirections,

        @Valid Coordinate coordinates,

        @NotNull
        Boolean isShelf,

        @NotNull
        Boolean isRoad
) {
}
