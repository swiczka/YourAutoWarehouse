package io.swiczka.github.apiwarehouse.packageitem.response;

import io.swiczka.github.apiwarehouse.enums.PackageStatus;

public record PackageItemResponse(
        Long id,
        String name,
        Long inboundOrderId,
        Long outboundOrderId,
        Integer x,
        Integer y,
        PackageStatus status,
        Long layoutId
) {
}
