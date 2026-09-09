package io.swiczka.github.sharedcommon.events;

import java.util.List;
import java.util.UUID;

public record InboundOrderCreatedEvent(
    Long orderId,
    UUID operatorId,
    Long companyId,
    Long layoutId,
    List<String> packageNames
) {

}
