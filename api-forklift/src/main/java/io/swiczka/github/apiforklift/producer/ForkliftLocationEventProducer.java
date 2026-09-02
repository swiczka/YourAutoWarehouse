package io.swiczka.github.apiforklift.producer;

import io.swiczka.github.sharedcommon.topics.KafkaTopics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import io.swiczka.github.sharedcommon.events.ForkliftLocationEvent;

@Component
public class ForkliftLocationEventProducer {
    private final KafkaTemplate<String, ForkliftLocationEvent> kafkaTemplate;
    private static final Logger log = LoggerFactory.getLogger(ForkliftLocationEventProducer.class);

    @Autowired
    public ForkliftLocationEventProducer(KafkaTemplate<String, ForkliftLocationEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;

    }

    public void sendNewLocation(final ForkliftLocationEvent event){
        kafkaTemplate.send(KafkaTopics.FORKLIFT_LOCATION_UPDATED, event.forkliftId().toString(), event);
        log.info("Sent ForkliftLocationEvent for forkliftId={}", event.forkliftId());
    }
}
