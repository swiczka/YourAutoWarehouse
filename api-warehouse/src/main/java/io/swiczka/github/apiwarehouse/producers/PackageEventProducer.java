package io.swiczka.github.apiwarehouse.producers;

import io.swiczka.github.sharedcommon.events.PackageAllocatedEvent;
import io.swiczka.github.sharedcommon.topics.KafkaTopics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PackageEventProducer {
    private final KafkaTemplate<String, PackageAllocatedEvent> kafkaTemplate;
    private static final Logger log = LoggerFactory.getLogger(PackageEventProducer.class);

    @Autowired
    public PackageEventProducer(KafkaTemplate<String, PackageAllocatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendPackageAllocated(final PackageAllocatedEvent event){
        kafkaTemplate.send(KafkaTopics.PACKAGE_ALLOCATED, event.packageId().toString(), event);
        log.info("Sent PackageAllocatedEvent for packageId={}", event.packageId());
    }
}
