package io.swiczka.github.apiwarehouse.layout.response;

import java.time.Instant;

public record LayoutIdReadResponse(
        Long id,
        Instant createdAt
) {
}
