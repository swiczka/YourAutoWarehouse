package io.swiczka.github.apiwarehouse.mapper;

import io.swiczka.github.apiwarehouse.entity.Layout;
import io.swiczka.github.apiwarehouse.layout.response.LayoutReadResponse;
import io.swiczka.github.sharedcommon.dto.WarehouseLayoutDto;
import io.swiczka.github.sharedcommon.events.LayoutLoadedEvent;
import io.swiczka.github.sharedcommon.events.LayoutSavedEvent;

public final class LayoutMapper {

    private LayoutMapper() {
        // Utility class
    }

    public static LayoutReadResponse toDto(final Layout entity) {
        if (entity == null) {
            return null;
        }

        return new LayoutReadResponse(
                entity.getId(),
                entity.getUserId(),
                entity.getCreatedAt(),
                entity.getForkliftNumber(),
                entity.getGridData() != null ?
                        entity.getGridData().stream()
                                .map(GridDataMapper::toDto)
                                .toList() : null
        );
    }

    public static Layout toEntity(final LayoutReadResponse dto) {
        if (dto == null) {
            return null;
        }

        final Layout layout = new Layout(
                dto.userId(),
                dto.createdAt(),
                dto.forkliftNumber(),
                dto.gridData() != null ?
                        dto.gridData().stream()
                                .map(GridDataMapper::toDomain)
                                .toList() : null
        );
        layout.setId(dto.id());

        return layout;
    }

    public static LayoutSavedEvent toEvent(final Layout entity) {
        if (entity == null) {
            return null;
        }

        return new LayoutSavedEvent(
                entity.getId(),
                entity.getUserId(),
                entity.getForkliftNumber(),
                entity.getGridData() != null ?
                        entity.getGridData().stream()
                                .map(GridDataMapper::toEvent)
                                .toList() : null
        );
    }

    public static LayoutLoadedEvent toLoadedEvent(final Layout entity) {
        if (entity == null) {
            return null;
        }

        return new LayoutLoadedEvent(
                entity.getId(),
                entity.getUserId(),
                entity.getForkliftNumber(),
                entity.getGridData() != null ?
                        entity.getGridData().stream()
                                .map(GridDataMapper::toEvent)
                                .toList() : null
        );
    }

    public static WarehouseLayoutDto toSharedDto(final Layout entity) {
        if (entity == null) {
            return null;
        }

        return new WarehouseLayoutDto(
                entity.getId(),
                entity.getUserId(),
                entity.getCreatedAt(),
                entity.getForkliftNumber(),
                entity.getGridData() != null ?
                        entity.getGridData().stream()
                                .map(GridDataMapper::toEvent)
                                .toList() : null
        );
    }

}
