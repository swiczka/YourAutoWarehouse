package io.swiczka.github.apiwarehouse.mapper;

import io.swiczka.github.apiwarehouse.entity.Layout;
import io.swiczka.github.apiwarehouse.layout.response.LayoutReadResponse;

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

}
