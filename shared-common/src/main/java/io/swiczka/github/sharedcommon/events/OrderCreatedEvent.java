package io.swiczka.github.sharedcommon.events;

import java.util.UUID;

public record OrderCreatedEvent(
    Long orderId,
    UUID operatorId,
    Long companyId
    //status I guess?
) {

}
