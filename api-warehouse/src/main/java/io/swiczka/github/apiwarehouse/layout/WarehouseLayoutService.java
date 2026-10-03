package io.swiczka.github.apiwarehouse.layout;

import io.swiczka.github.apiwarehouse.dao.LayoutDAO;
import io.swiczka.github.apiwarehouse.domain.GridData;
import io.swiczka.github.apiwarehouse.entity.Layout;
import io.swiczka.github.apiwarehouse.exceptions.ForbiddenException;
import io.swiczka.github.apiwarehouse.exceptions.LayoutNotFoundException;
import io.swiczka.github.apiwarehouse.layout.dto.GridDataDto;
import io.swiczka.github.apiwarehouse.layout.response.LayoutIdReadResponse;
import io.swiczka.github.sharedcommon.dto.LayoutOwnerDto;
import io.swiczka.github.sharedcommon.dto.WarehouseLayoutDto;
import io.swiczka.github.apiwarehouse.layout.response.LayoutReadResponse;
import io.swiczka.github.apiwarehouse.mapper.GridDataMapper;
import io.swiczka.github.apiwarehouse.mapper.LayoutMapper;
import io.swiczka.github.apiwarehouse.producers.LayoutEventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class WarehouseLayoutService {
    private final LayoutDAO layoutDAO;
    private final LayoutEventProducer layoutEventProducer;

    @Autowired
    public WarehouseLayoutService(final LayoutDAO layoutDAO, final LayoutEventProducer layoutEventProducer) {
        this.layoutDAO = layoutDAO;
        this.layoutEventProducer = layoutEventProducer;
    }

    public LayoutReadResponse saveNewLayout(final List<GridDataDto> gridDataDto, final UUID operatorId) {
        final List<GridData> gridData = gridDataDto.stream()
                .map(GridDataMapper::toDomain)
                .toList();
        final Layout newLayout = new Layout(operatorId, Instant.now(), 5, gridData);
        layoutDAO.save(newLayout);

        layoutEventProducer.sendLayoutSaved(LayoutMapper.toEvent(newLayout));
        return LayoutMapper.toDto(newLayout);
    }

    public LayoutReadResponse getLatestUserLayout(final UUID userId) {
        final Layout layout = layoutDAO.findLatestByUserId(userId)
                .orElseThrow(() -> new LayoutNotFoundException(userId));
        layoutEventProducer.sendLayoutLoaded(LayoutMapper.toLoadedEvent(layout));
        return LayoutMapper.toDto(layout);
    }

    public LayoutReadResponse getLayoutById(final Long layoutId, final UUID userId) {
        final Layout layout = layoutDAO.findById(layoutId)
                .orElseThrow(() -> new LayoutNotFoundException(layoutId));

        final boolean isOwner = layout.getUserId().equals(userId);
        if (!isOwner) {
            throw new ForbiddenException("Access denied to layout with id " + layoutId);
        }

        layoutEventProducer.sendLayoutLoaded(LayoutMapper.toLoadedEvent(layout));
        return LayoutMapper.toDto(layout);
    }

    public WarehouseLayoutDto getLayoutByIdInternal(final Long layoutId) {
        final Layout layout = layoutDAO.findById(layoutId)
                .orElseThrow(() -> new LayoutNotFoundException(layoutId));
        return LayoutMapper.toSharedDto(layout);
    }

    public LayoutReadResponse updateForkliftNumber(final Long layoutId, final int forkliftNumber) {
        final Layout layout = layoutDAO.findById(layoutId)
                .orElseThrow(() -> new LayoutNotFoundException(layoutId));
        layout.setForkliftNumber(forkliftNumber);
        layoutDAO.save(layout);
        return LayoutMapper.toDto(layout);
    }

    public LayoutOwnerDto getLayoutOwner(final Long id) {
        return layoutDAO.findOwnerById(id)
                .orElseThrow(() -> new LayoutNotFoundException(id));
    }

    public List<LayoutIdReadResponse> getUserLayoutIds(UUID guestId) {
        return layoutDAO.findUserLayoutIds(guestId);
    }
}
