package io.swiczka.github.apiwarehouse.layout;

import io.swiczka.github.apiwarehouse.dao.LayoutDAO;
import io.swiczka.github.apiwarehouse.domain.GridData;
import io.swiczka.github.apiwarehouse.entity.Layout;
import io.swiczka.github.apiwarehouse.layout.dto.GridDataDto;
import io.swiczka.github.apiwarehouse.mapper.GridDataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class WarehouseLayoutService {
    private final LayoutDAO layoutDAO;

    @Autowired
    public WarehouseLayoutService(LayoutDAO layoutDAO) {
        this.layoutDAO = layoutDAO;
    }

    public void saveNewLayout(List<GridDataDto> gridDataDto){
        List<GridData> gridData = gridDataDto.stream()
                .map(GridDataMapper::toDomain)
                .toList();
        Layout newLayout = new Layout(UUID.randomUUID(), Instant.now(), gridData);
        layoutDAO.save(newLayout);
    }

    public List<GridDataDto> getUserLayout(UUID userId){
        Layout layout = layoutDAO.findByUserId(userId);
        if(layout == null){
            return null;
        }
        List<GridData> gridData = layout.getGridData();
        List<GridDataDto> gridDataDto = gridData.stream()
                .map(GridDataMapper::toDto)
                .toList();
        return gridDataDto;
    }
}
