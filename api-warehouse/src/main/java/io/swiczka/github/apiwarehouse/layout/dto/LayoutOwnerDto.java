package io.swiczka.github.apiwarehouse.layout.dto;

import java.util.UUID;

public record LayoutOwnerDto(
        Long id,
        UUID userId
) {
}
