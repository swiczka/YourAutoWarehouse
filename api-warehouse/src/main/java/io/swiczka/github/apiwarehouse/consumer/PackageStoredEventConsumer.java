package io.swiczka.github.apiwarehouse.consumer;

import io.swiczka.github.apiwarehouse.packageitem.PackageItemService;
import io.swiczka.github.sharedcommon.events.PackageStoredEvent;
import io.swiczka.github.sharedcommon.topics.KafkaTopics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PackageStoredEventConsumer {
    private static final Logger log = LoggerFactory.getLogger(PackageStoredEventConsumer.class);
    private final PackageItemService packageService;

    @Autowired
    public PackageStoredEventConsumer(final PackageItemService packageService) {
        this.packageService = packageService;
    }

    @KafkaListener(topics = KafkaTopics.PACKAGE_STORED, groupId = "warehouse-service")
    public void handlePackageLocationUpdated(final PackageStoredEvent event) {
        log.info("Received PackageStoredEvent for packageId={}", event.packageId());
        packageService.markPackageAsStored(event.packageId(), event.x(), event.y());
    }
}
