package io.swiczka.github.apiwarehouse.mapper;

import io.swiczka.github.apiwarehouse.domain.GridData;
import io.swiczka.github.apiwarehouse.layout.dto.GridDataDto;
import io.swiczka.github.sharedcommon.events.GridCellEvent;

public final class GridDataMapper {

    public GridDataMapper() {
    }

    public static GridDataDto toDto(final GridData domain){
        if(domain == null) return null;
        return new GridDataDto(domain.allowedDirections(),
                                domain.coordinates(),
                                domain.isShelf(),
                                domain.isRoad());
    }

    public static GridData toDomain(final GridDataDto dto){
        if(dto == null) return null;
        return new GridData(dto.allowedDirections(),
                dto.coordinates(),
                dto.isShelf(),
                dto.isRoad());
    }

    public static GridCellEvent toEvent(final GridData domain){
        if(domain == null) return null;
        return new GridCellEvent(domain.allowedDirections(),
                domain.coordinates(),
                domain.isRoad(),
                domain.isShelf());
    }
}
