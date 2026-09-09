package io.swiczka.github.apiwarehouse.packageitem;

import io.swiczka.github.apiwarehouse.dao.LayoutDAO;
import io.swiczka.github.apiwarehouse.dao.PackageItemDAO;
import io.swiczka.github.apiwarehouse.entity.Layout;
import io.swiczka.github.apiwarehouse.entity.PackageItem;
import io.swiczka.github.apiwarehouse.enums.PackageStatus;
import io.swiczka.github.apiwarehouse.exceptions.LayoutNotFoundException;
import io.swiczka.github.apiwarehouse.producers.PackageEventProducer;
import io.swiczka.github.apiwarehouse.strategy.PackagePlacementStrategy;
import io.swiczka.github.sharedcommon.events.PackageAllocatedEvent;
import io.swiczka.github.sharedcommon.helpers.Coordinate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional
public class PackageItemService {

    private final PackageItemDAO packageDAO;
    private final PackagePlacementStrategy packagePlacementStrategy;
    private final LayoutDAO layoutDAO;
    private final PackageEventProducer eventProducer;

    @Autowired
    public PackageItemService(final PackageItemDAO packageDAO, PackagePlacementStrategy packagePlacementStrategy, LayoutDAO layoutDAO, PackageEventProducer eventProducer) {
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
}
