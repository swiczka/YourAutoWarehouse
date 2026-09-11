package io.swiczka.github.apiwarehouse.mapper;

import io.swiczka.github.apiwarehouse.entity.PackageItem;
import io.swiczka.github.apiwarehouse.packageitem.response.PackageItemResponse;

public final class PackageItemMapper {

    private PackageItemMapper() {
        // Utility class
    }

    public static PackageItemResponse toDto(final PackageItem entity) {
        if (entity == null) {
            return null;
        }

        return new PackageItemResponse(
                entity.getId(),
                entity.getName(),
                entity.getInboundOrderId(),
                entity.getOutboundOrderId(),
                entity.getX(),
                entity.getY(),
                entity.getStatus(),
                entity.getLayoutId()
        );
    }
}
