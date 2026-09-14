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
import io.swiczka.github.sharedcommon.events.OutboundOrderCreatedEvent;
import io.swiczka.github.sharedcommon.events.PackageAllocatedEvent;
import io.swiczka.github.sharedcommon.events.PackageShouldBeSentEvent;
import io.swiczka.github.sharedcommon.helpers.Coordinate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class PackageItemService {

    private static final Logger log = LoggerFactory.getLogger(PackageItemService.class);

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

        final Layout layout = layoutDAO.findById(layoutId)
                .orElseThrow(() -> new LayoutNotFoundException(layoutId));

        final Set<Coordinate> occupiedSpots = packageDAO.findOccupiedCoordinates(layoutId);
        final int currentX = 0;
        final int currentY = layout.getMaxY();

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

            final PackageAllocatedEvent event = new PackageAllocatedEvent(
                    item.getId(),
                    layoutId,
                    inboundOrderId,
                    name,
                    PackageStatus.ALLOCATED.name(),
                    currentX,
                    currentY,
                    targetSpot.x(),
                    targetSpot.y()
            );

            eventProducer.sendPackageAllocated(event);
        }
    }

    public void handleOutboundOrder(OutboundOrderCreatedEvent event) {
        final Layout layout = layoutDAO.findById(event.layoutId())
                .orElseThrow(() -> new LayoutNotFoundException(event.layoutId()));

        if(!layout.getUserId().equals(event.operatorId())){
            //TODO in future it could send an event about rejected order - for now it's just ignored
            throw new ForbiddenException("Access denied to packages for order with id " + event.orderId());
        }

        final int targetX = layout.getMaxX();
        final int targetY = layout.getMaxY();

        for(Long pkgId : event.packageIds()){
            Optional<PackageItem> optPkg = packageDAO.findById(pkgId);
            if(optPkg.isEmpty()){
                //silent error
                log.warn("Tried sending non-existing package {}", pkgId);
                continue;
            }

            PackageItem pkg = optPkg.get();

            pkg.setStatus(PackageStatus.SENDING);
            pkg.setOutboundOrderId(event.orderId());

            packageDAO.save(pkg);

            PackageShouldBeSentEvent newEvent = new PackageShouldBeSentEvent(
                pkg.getId(),
                layout.getId(),
                event.orderId(),
                pkg.getX(),
                pkg.getY(),
                targetX,
                targetY
            );

            eventProducer.sendPackageShouldBeSent(newEvent);
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

    public void markPackageAsStored(final Long packageId, final int x, final int y) {
        final Optional<PackageItem> packageOpt = packageDAO.findById(packageId);
        if (packageOpt.isEmpty()) {
            log.warn("Cannot store package. Package with ID: {} not found", packageId);
            return;
        }

        final PackageItem pkg = packageOpt.get();
        pkg.setX(x);
        pkg.setY(y);
        pkg.setStatus(PackageStatus.STORED);
        packageDAO.save(pkg);
    }

    public void markPackageAsDropped(Long packageId) {
        final Optional<PackageItem> packageOpt = packageDAO.findById(packageId);
        if (packageOpt.isEmpty()) {
            log.warn("Cannot drop package. Package with ID: {} not found", packageId);
            return;
        }
        final PackageItem pkg = packageOpt.get();
        pkg.setStatus(PackageStatus.DROPPED);
        pkg.setX(null);
        pkg.setY(null);

        packageDAO.save(pkg);
    }

    public List<PackageItemResponse> getPackagesByOutboundOrderId(Long outboundOrderId, UUID guestId) {
        final List<PackageItem> packages = packageDAO.findByOutboundOrderId(outboundOrderId);
        if (packages.isEmpty()) {
            return List.of();
        }

        final Long layoutId = packages.getFirst().getLayoutId();
        final LayoutOwnerDto owner = layoutDAO.findOwnerById(layoutId)
                .orElseThrow(() -> new LayoutNotFoundException(layoutId));

        final boolean isOwner = owner.userId().equals(guestId);
        if (!isOwner) {
            throw new ForbiddenException("Access denied to packages for order with id " + outboundOrderId);
        }

        return packages.stream()
                .map(PackageItemMapper::toDto)
                .toList();
    }
}
