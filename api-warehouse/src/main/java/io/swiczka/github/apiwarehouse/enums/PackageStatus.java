package io.swiczka.github.apiwarehouse.enums;

public enum PackageStatus {
    PENDING, //package arrived but there is no place
    ALLOCATED, //package arrived and target location has been calculated
    STORED, //package is being stored
    SENDING, //package is being transported to outbound zone
    DROPPED //package is no longer stored in warehouse
}
