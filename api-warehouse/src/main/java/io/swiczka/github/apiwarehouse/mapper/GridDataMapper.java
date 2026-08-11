package io.swiczka.github.apiwarehouse.mapper;

import io.swiczka.github.apiwarehouse.domain.GridData;
import io.swiczka.github.apiwarehouse.layout.dto.GridDataDto;

public class GridDataMapper {
    public static GridDataDto toDto(GridData domain){
        return new GridDataDto(domain.allowedDirections(),
                                domain.coordinates(),
                                domain.isShelf(),
                                domain.isRoad());
    }

    public static GridData toDomain(GridDataDto dto){
        return new GridData(dto.allowedDirections(),
                dto.coordinates(),
                dto.isShelf(),
                dto.isRoad());
    }
}
