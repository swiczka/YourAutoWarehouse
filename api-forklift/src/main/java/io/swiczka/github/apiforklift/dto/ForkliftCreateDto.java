package io.swiczka.github.apiforklift.dto;

import java.util.UUID;

public record ForkliftCreateDto(
        Long layoutId,
        UUID operatorId
) {
}
