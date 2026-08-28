package io.swiczka.github.apiforklift.enums;

public enum ForkliftStatus {
    READY, //just picked a task, ready for action
    MOVING, //moving with a task, ready for new task
    CARRYING, //carrying a package
    RETURNING, //returning to the garage with no task
    IN_GARAGE, //in a garage waiting for task
    PICKING, //picking a package
    DROPPING //dropping a package
}
