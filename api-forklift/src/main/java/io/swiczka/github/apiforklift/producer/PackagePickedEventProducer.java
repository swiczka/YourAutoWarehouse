package io.swiczka.github.apiforklift.producer;

import io.swiczka.github.sharedcommon.events.PackagePickedEvent;
import io.swiczka.github.sharedcommon.topics.KafkaTopics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PackagePickedEventProducer {
    private final KafkaTemplate<String, PackagePickedEvent> kafkaTemplate;
    private static final Logger log = LoggerFactory.getLogger(PackagePickedEventProducer.class);

    @Autowired
    public PackagePickedEventProducer(KafkaTemplate<String, PackagePickedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendPackagePickedEvent(final PackagePickedEvent event){
        kafkaTemplate.send(KafkaTopics.PACKAGE_PICKED, event.packageId().toString(), event);
        log.info("Sent PackagePickedEvent for packageId={}", event.packageId());
    }
}
