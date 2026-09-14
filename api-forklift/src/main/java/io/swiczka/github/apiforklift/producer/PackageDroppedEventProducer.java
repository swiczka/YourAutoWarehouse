package io.swiczka.github.apiforklift.producer;

import io.swiczka.github.sharedcommon.events.PackageDroppedEvent;
import io.swiczka.github.sharedcommon.topics.KafkaTopics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PackageDroppedEventProducer {
    private final KafkaTemplate<String, PackageDroppedEvent> kafkaTemplate;
    private static final Logger log = LoggerFactory.getLogger(PackageDroppedEventProducer.class);

    @Autowired
    public PackageDroppedEventProducer(KafkaTemplate<String, PackageDroppedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendPackageDroppedEvent(final PackageDroppedEvent event){
        kafkaTemplate.send(KafkaTopics.PACKAGE_DROPPED, event.packageId().toString(), event);
        log.info("Sent PackageDroppedEvent for packageId={}", event.packageId());
    }
}
