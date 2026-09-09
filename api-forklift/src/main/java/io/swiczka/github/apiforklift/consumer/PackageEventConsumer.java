package io.swiczka.github.apiforklift.consumer;

import io.swiczka.github.apiforklift.domain.ForkliftTask;
import io.swiczka.github.apiforklift.domain.SimulationLayout;
import io.swiczka.github.apiforklift.dto.ForkliftTaskCreateDto;
import io.swiczka.github.apiforklift.simulation.ForkliftTaskRegistry;
import io.swiczka.github.apiforklift.simulation.LayoutCache;
import io.swiczka.github.sharedcommon.events.PackageAllocatedEvent;
import io.swiczka.github.sharedcommon.topics.KafkaTopics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PackageEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(PackageEventConsumer.class);
    private static final int DELIVERY_ZONE_X = 0;

    private final ForkliftTaskRegistry taskRegistry;
    private final LayoutCache layoutCache;

    @Autowired
    public PackageEventConsumer(
            final ForkliftTaskRegistry taskRegistry,
            final LayoutCache layoutCache
    ) {
        this.taskRegistry = taskRegistry;
        this.layoutCache = layoutCache;
    }

    @KafkaListener(topics = KafkaTopics.PACKAGE_ALLOCATED, groupId = "forklift-service")
    public void handlePackageAllocated(final PackageAllocatedEvent event) {
        log.info("Received PackageAllocatedEvent for packageId={}, layoutId={}, target=({}, {})",
                event.packageId(), event.layoutId(), event.x(), event.y());

        final Optional<SimulationLayout> layoutOpt = layoutCache.get(event.layoutId());
        final int sourceY = layoutOpt.map(SimulationLayout::getMaxY).orElse(0);

        final ForkliftTaskCreateDto taskDto = new ForkliftTaskCreateDto(
                event.packageId(),
                event.layoutId(),
                DELIVERY_ZONE_X,
                sourceY,
                event.x(),
                event.y()
        );

        final ForkliftTask createdTask = taskRegistry.add(taskDto);
        log.info("Created ForkliftTask id={} for packageId={} from ({}, {}) to ({}, {})",
                createdTask != null ? createdTask.getTaskId() : null,
                event.packageId(),
                DELIVERY_ZONE_X,
                sourceY,
                event.x(),
                event.y());
    }
}
