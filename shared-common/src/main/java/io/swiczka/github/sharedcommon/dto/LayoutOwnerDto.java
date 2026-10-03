package io.swiczka.github.sharedcommon.dto;

import java.io.Serializable;
import java.util.UUID;

public record LayoutOwnerDto(Long id, UUID userId) implements Serializable {
}
