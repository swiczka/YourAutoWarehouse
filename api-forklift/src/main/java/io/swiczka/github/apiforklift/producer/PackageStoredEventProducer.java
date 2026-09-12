package io.swiczka.github.apiforklift.producer;

import io.swiczka.github.sharedcommon.events.PackageStoredEvent;
import io.swiczka.github.sharedcommon.topics.KafkaTopics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PackageStoredEventProducer {
    private final KafkaTemplate<String, PackageStoredEvent> kafkaTemplate;
    private static final Logger log = LoggerFactory.getLogger(PackageStoredEventProducer.class);

    @Autowired
    public PackageStoredEventProducer(KafkaTemplate<String, PackageStoredEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendPackageStoredEvent(final PackageStoredEvent event){
        kafkaTemplate.send(KafkaTopics.PACKAGE_STORED, event.packageId().toString(), event);
        log.info("Sent PackageStoredEvent for packageId={}", event.packageId());
    }
}
