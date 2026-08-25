package io.swiczka.github.apiforklift.dto;

public record ForkliftTaskCreateDto(
        Long packageItemId,
        int sourceX,
        int sourceY,
        int targetX,
        int targetY
) {
}
