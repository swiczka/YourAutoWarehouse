package io.swiczka.github.apiwarehouse.producers;

import io.swiczka.github.sharedcommon.events.PackageAllocatedEvent;
import io.swiczka.github.sharedcommon.events.PackageShouldBeSentEvent;
import io.swiczka.github.sharedcommon.topics.KafkaTopics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PackageEventProducer {
    private final KafkaTemplate<String, PackageAllocatedEvent> kafkaAllocatedTemplate;
    private final KafkaTemplate<String, PackageShouldBeSentEvent> kafkaShouldBeSentTemplate;
    private static final Logger log = LoggerFactory.getLogger(PackageEventProducer.class);

    @Autowired
    public PackageEventProducer(final KafkaTemplate<String, PackageAllocatedEvent> kafkaAllocatedTemplate,
                                final KafkaTemplate<String, PackageShouldBeSentEvent> kafkaShouldBeSentTemplate) {
        this.kafkaAllocatedTemplate = kafkaAllocatedTemplate;
        this.kafkaShouldBeSentTemplate = kafkaShouldBeSentTemplate;
    }

    public void sendPackageAllocated(final PackageAllocatedEvent event){
        kafkaAllocatedTemplate.send(KafkaTopics.PACKAGE_ALLOCATED, event.packageId().toString(), event);
        log.info("Sent PackageAllocatedEvent for packageId={}", event.packageId());
    }

    public void sendPackageShouldBeSent(final PackageShouldBeSentEvent event){
        kafkaShouldBeSentTemplate.send(KafkaTopics.PACKAGE_SHOULD_BE_SENT, event.packageId().toString(), event);
        log.info("Sent PackageShouldBeSentEvent for packageId={}", event.packageId());
    }
}
