package io.swiczka.github.apiforklift.consumer;

import io.swiczka.github.apiforklift.client.WarehouseLayoutClient;
import io.swiczka.github.apiforklift.domain.ForkliftTask;
import io.swiczka.github.apiforklift.dto.ForkliftTaskCreateDto;
import io.swiczka.github.apiforklift.mapper.SimulationLayoutMapper;
import io.swiczka.github.apiforklift.simulation.ForkliftTaskRegistry;
import io.swiczka.github.apiforklift.simulation.LayoutCache;
import io.swiczka.github.sharedcommon.events.PackageAllocatedEvent;
import io.swiczka.github.sharedcommon.events.PackageShouldBeSentEvent;
import io.swiczka.github.sharedcommon.helpers.TaskType;
import io.swiczka.github.sharedcommon.topics.KafkaTopics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PackageEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(PackageEventConsumer.class);

    private final ForkliftTaskRegistry taskRegistry;
    private final LayoutCache layoutCache;
    private final WarehouseLayoutClient warehouseLayoutClient;

    @Autowired
    public PackageEventConsumer(
            final ForkliftTaskRegistry taskRegistry,
            final LayoutCache layoutCache,
            final WarehouseLayoutClient warehouseLayoutClient
    ) {
        this.taskRegistry = taskRegistry;
        this.layoutCache = layoutCache;
        this.warehouseLayoutClient = warehouseLayoutClient;
    }

    @KafkaListener(topics = KafkaTopics.PACKAGE_ALLOCATED, groupId = "forklift-service")
    public void handlePackageAllocated(final PackageAllocatedEvent event) {
        log.info("Received PackageAllocatedEvent for packageId={}, layoutId={}, current=({}, {}), target=({}, {})",
                event.packageId(), event.layoutId(), event.currentX(), event.currentY(), event.targetX(), event.targetY());

        ensureLayoutLoaded(event.layoutId());

        final ForkliftTaskCreateDto taskDto = new ForkliftTaskCreateDto(
                event.packageId(),
                event.layoutId(),
                event.currentX(),
                event.currentY(),
                event.targetX(),
                event.targetY(),
                TaskType.INBOUND
        );

        final ForkliftTask createdTask = taskRegistry.add(taskDto);
        log.info("Created ForkliftTask id={} for packageId={} from ({}, {}) to ({}, {})",
                createdTask != null ? createdTask.getTaskId() : null,
                event.packageId(),
                event.currentX(),
                event.currentY(),
                event.targetX(),
                event.targetY());
    }

    @KafkaListener(topics = KafkaTopics.PACKAGE_SHOULD_BE_SENT, groupId = "forklift-service")
    public void handlePackageShouldBeSent(final PackageShouldBeSentEvent event) {
        log.info("Received PackageShouldBeSentEvent for packageId={}, layoutId={}",
                event.packageId(), event.layoutId());

        ensureLayoutLoaded(event.layoutId());

        final ForkliftTaskCreateDto taskDto = new ForkliftTaskCreateDto(
                event.packageId(),
                event.layoutId(),
                event.currentX(),
                event.currentY(),
                event.targetX(),
                event.targetY(),
                TaskType.OUTBOUND
        );

        final ForkliftTask createdTask = taskRegistry.add(taskDto);
        log.info("Created ForkliftTask id={} for packageId={} from ({}, {}) to ({}, {})",
                createdTask != null ? createdTask.getTaskId() : null,
                event.packageId(),
                event.currentX(),
                event.currentY(),
                event.targetX(),
                event.targetY());
    }

    private void ensureLayoutLoaded(final Long layoutId) {
        if (layoutId == null) {
            return;
        }

        if (layoutCache.contains(layoutId)) {
            layoutCache.touch(layoutId);
            return;
        }

        log.info("LayoutId={} not in cache, fetching from warehouse API...", layoutId);
        warehouseLayoutClient.getLayoutById(layoutId)
                .map(SimulationLayoutMapper::toSimulation)
                .ifPresent(layoutCache::put);
    }
}
