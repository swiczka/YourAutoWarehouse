package io.swiczka.github.apiforklift.response;

import io.swiczka.github.apiforklift.enums.ForkliftStatus;

import java.util.UUID;

public record ForkliftResponse(
        Long id,
        Long layoutId,
        UUID operatorId,
        ForkliftStatus status,
        int x,
        int y
) {
}
