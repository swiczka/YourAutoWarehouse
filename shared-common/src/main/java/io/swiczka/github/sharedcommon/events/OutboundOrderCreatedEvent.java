package io.swiczka.github.sharedcommon.events;

import java.util.List;
import java.util.UUID;

public record OutboundOrderCreatedEvent(
        Long orderId,
        UUID operatorId,
        Long companyId,
        Long layoutId,
        List<Long> packageIds
) {
}
