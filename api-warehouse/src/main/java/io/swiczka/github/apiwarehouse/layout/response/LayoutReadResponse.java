package io.swiczka.github.apiwarehouse.layout.response;
import io.swiczka.github.apiwarehouse.layout.dto.GridDataDto;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record LayoutReadResponse(
Long id,
UUID userId,
Instant createdAt,
Integer forkliftNumber,
List<GridDataDto> gridData
) { }
