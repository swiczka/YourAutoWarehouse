package io.swiczka.github.apigateway.consumer;

import io.swiczka.github.apigateway.stomp.PackageWebsocketPublisher;
import io.swiczka.github.sharedcommon.events.PackageAllocatedEvent;
import io.swiczka.github.sharedcommon.events.PackagePickedEvent;
import io.swiczka.github.sharedcommon.events.PackageStoredEvent;
import io.swiczka.github.sharedcommon.topics.KafkaTopics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PackageLocationEventConsumer {
    private static final Logger log = LoggerFactory.getLogger(PackageLocationEventConsumer.class);

    private final PackageWebsocketPublisher websocketPublisher;

    public PackageLocationEventConsumer(final PackageWebsocketPublisher websocketPublisher) {
        this.websocketPublisher = websocketPublisher;
    }

    @KafkaListener(topics = KafkaTopics.PACKAGE_STORED, groupId = "gateway-service")
    public void handlePackageLocationUpdated(final PackageStoredEvent event) {
        log.info("Received PackageStoredEvent for packageId={}", event.packageId());
        websocketPublisher.publishLocation(event);
    }

    @KafkaListener(topics = KafkaTopics.PACKAGE_ALLOCATED, groupId = "gateway-service")
    public void handlePackageAllocated(final PackageAllocatedEvent event) {
        log.info("Received PackageAllocatedEvent for packageId={}", event.packageId());
        websocketPublisher.publishAllocated(event);
    }

    @KafkaListener(topics = KafkaTopics.PACKAGE_PICKED, groupId = "gateway-service")
    public void handlePackagePicked(final PackagePickedEvent event) {
        log.info("Received PackagePickedEvent for packageId={}", event.packageId());
        websocketPublisher.publishPicked(event);
    }
}
