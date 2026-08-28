package io.swiczka.github.apiwarehouse.layout;

import io.swiczka.github.apiwarehouse.dao.LayoutDAO;
import io.swiczka.github.apiwarehouse.domain.GridData;
import io.swiczka.github.apiwarehouse.entity.Layout;
import io.swiczka.github.apiwarehouse.exceptions.LayoutNotFoundException;
import io.swiczka.github.apiwarehouse.layout.dto.GridDataDto;
import io.swiczka.github.apiwarehouse.layout.response.LayoutReadResponse;
import io.swiczka.github.apiwarehouse.mapper.GridDataMapper;
import io.swiczka.github.apiwarehouse.mapper.LayoutMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional //spring.framework transactional
    public LayoutReadResponse saveNewLayout(List<GridDataDto> gridDataDto, UUID operatorId){
        List<GridData> gridData = gridDataDto.stream()
                .map(GridDataMapper::toDomain)
                .toList();
        Layout newLayout = new Layout(operatorId, Instant.now(), 5, gridData);
        layoutDAO.save(newLayout);
        return LayoutMapper.toDto(newLayout);
    }

    public LayoutReadResponse getLatestUserLayout(final UUID userId) {
        return layoutDAO.findLatestByUserId(userId)
                .map(LayoutMapper::toDto)
                .orElseThrow(() -> new LayoutNotFoundException(userId));
    }

    public LayoutReadResponse getLayoutById(final Long layoutId) {
        return layoutDAO.findById(layoutId)
                .map(LayoutMapper::toDto)
                .orElseThrow(() -> new LayoutNotFoundException(layoutId));
    }

    @Transactional
    public LayoutReadResponse updateForkliftNumber(Long layoutId, int forkliftNumber){
        Layout layout = layoutDAO.findById(layoutId)
                .orElseThrow(() -> new LayoutNotFoundException(layoutId));
        layout.setForkliftNumber(forkliftNumber);
        layoutDAO.save(layout);
        return LayoutMapper.toDto(layout);
    }

}
