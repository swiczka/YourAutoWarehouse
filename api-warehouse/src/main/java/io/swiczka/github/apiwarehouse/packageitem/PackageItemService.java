package io.swiczka.github.apiwarehouse.packageitem;

import io.swiczka.github.apiwarehouse.dao.LayoutDAO;
import io.swiczka.github.apiwarehouse.dao.PackageItemDAO;
import io.swiczka.github.apiwarehouse.entity.Layout;
import io.swiczka.github.apiwarehouse.entity.PackageItem;
import io.swiczka.github.apiwarehouse.enums.PackageStatus;
import io.swiczka.github.apiwarehouse.exceptions.ForbiddenException;
import io.swiczka.github.apiwarehouse.exceptions.LayoutNotFoundException;
import io.swiczka.github.apiwarehouse.layout.dto.LayoutOwnerDto;
import io.swiczka.github.apiwarehouse.mapper.PackageItemMapper;
import io.swiczka.github.apiwarehouse.packageitem.response.PackageItemResponse;
import io.swiczka.github.apiwarehouse.producers.PackageEventProducer;
import io.swiczka.github.apiwarehouse.strategy.PackagePlacementStrategy;
import io.swiczka.github.sharedcommon.events.PackageAllocatedEvent;
import io.swiczka.github.sharedcommon.helpers.Coordinate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class PackageItemService {

    private final PackageItemDAO packageDAO;
    private final PackagePlacementStrategy packagePlacementStrategy;
    private final LayoutDAO layoutDAO;
    private final PackageEventProducer eventProducer;

    @Autowired
    public PackageItemService(
            final PackageItemDAO packageDAO,
            final PackagePlacementStrategy packagePlacementStrategy,
            final LayoutDAO layoutDAO,
            final PackageEventProducer eventProducer
    ) {
        this.packageDAO = packageDAO;
        this.packagePlacementStrategy = packagePlacementStrategy;
        this.layoutDAO = layoutDAO;
        this.eventProducer = eventProducer;
    }

    public void addNewPackages(
            final List<String> packageNames,
            final Long inboundOrderId,
            final Long layoutId
    ) {

        Layout layout = layoutDAO.findById(layoutId)
                .orElseThrow(() -> new LayoutNotFoundException(layoutId));

        final Set<Coordinate> occupiedSpots = packageDAO.findOccupiedCoordinates(layoutId);

        for (final String name : packageNames) {
            final Coordinate targetSpot = packagePlacementStrategy.findTargetLocation(layout, occupiedSpots);
            occupiedSpots.add(targetSpot);
            final PackageItem item = new PackageItem(
                    layoutId,
                    name,
                    inboundOrderId,
                    null, // outboundOrderId
                    targetSpot.x(),
                    targetSpot.y(),
                    PackageStatus.ALLOCATED
            );
            packageDAO.save(item);

            PackageAllocatedEvent event = new PackageAllocatedEvent(
                    item.getId(),
                    layoutId,
                    name,
                    targetSpot.x(),
                    targetSpot.y()
            );

            eventProducer.sendPackageAllocated(event);
        }
    }

    public List<PackageItemResponse> getPackagesByInboundOrderId(
            final Long inboundOrderId,
            final UUID userId
    ) {
        final List<PackageItem> packages = packageDAO.findByInboundOrderId(inboundOrderId);
        if (packages.isEmpty()) {
            return List.of();
        }

        final Long layoutId = packages.getFirst().getLayoutId();
        final LayoutOwnerDto owner = layoutDAO.findOwnerById(layoutId)
                .orElseThrow(() -> new LayoutNotFoundException(layoutId));

        final boolean isOwner = owner.userId().equals(userId);
        if (!isOwner) {
            throw new ForbiddenException("Access denied to packages for order with id " + inboundOrderId);
        }

        return packages.stream()
                .map(PackageItemMapper::toDto)
                .toList();
    }

    public List<PackageItemResponse> getPackagesByLayoutId(
            final Long layoutId,
            final UUID userId
    ) {
        final List<PackageItem> packages = packageDAO.findByLayoutId(layoutId);
        if (packages.isEmpty()) {
            return List.of();
        }

        final LayoutOwnerDto owner = layoutDAO.findOwnerById(layoutId)
                .orElseThrow(() -> new LayoutNotFoundException(layoutId));

        final boolean isOwner = owner.userId().equals(userId);
        if (!isOwner) {
            throw new ForbiddenException("Access denied to packages for layout with id " + layoutId);
        }

        return packages.stream()
                .map(PackageItemMapper::toDto)
                .toList();
    }
}
