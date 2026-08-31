package io.swiczka.github.apiwarehouse.producers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import io.swiczka.github.sharedcommon.events.LayoutSavedEvent;
import io.swiczka.github.sharedcommon.topics.KafkaTopics;

@Component
public class LayoutEventProducer {
    private final KafkaTemplate<String, LayoutSavedEvent> kafkaTemplate;
    private static final Logger log = LoggerFactory.getLogger(LayoutEventProducer.class);

    @Autowired
    public LayoutEventProducer(KafkaTemplate<String, LayoutSavedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;

    }

    public void sendLayoutSaved(final LayoutSavedEvent event){
        kafkaTemplate.send(KafkaTopics.LAYOUT_SAVED, event.layoutId().toString(), event);
        log.info("Sent LayoutSavedEvent for layoutId={}", event.layoutId());
    }
}
