package io.swiczka.github.sharedcommon.helpers;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record Coordinate(
        @NotNull(message = "X coordinate is required")
        @Min(value = 0, message = "X must be greater than or equal to 0")
        @Max(value = 64, message = "X must be less than or equal to 64")
        Integer x,

        @NotNull(message = "Y coordinate is required")
        @Min(value = 0, message = "Y must be greater than or equal to 0")
        @Max(value = 64, message = "Y must be less than or equal to 64")
        Integer y
) {
}
