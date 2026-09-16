package io.swiczka.github.sharedcommon.events;

import io.swiczka.github.sharedcommon.helpers.TaskType;

import java.time.Instant;

public record OrderCompleteEvent (Long orderId,
                                  TaskType taskType,
                                  Instant time) {

}
