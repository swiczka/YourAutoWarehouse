package io.swiczka.github.apiwarehouse.producers;

import io.swiczka.github.sharedcommon.events.LayoutForkliftNumberChangedEvent;
import io.swiczka.github.sharedcommon.events.LayoutLoadedEvent;
import io.swiczka.github.sharedcommon.events.LayoutSavedEvent;
import io.swiczka.github.sharedcommon.topics.KafkaTopics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class LayoutEventProducer {
    private final KafkaTemplate<String, LayoutSavedEvent> kafkaSavedTemplate;
    private final KafkaTemplate<String, LayoutLoadedEvent> kafkaLoadedTemplate;
    private final KafkaTemplate<String, LayoutForkliftNumberChangedEvent> kafkaForkliftNumberTemplate;
    private static final Logger log = LoggerFactory.getLogger(LayoutEventProducer.class);

    @Autowired
    public LayoutEventProducer(
            final KafkaTemplate<String, LayoutSavedEvent> kafkaSavedTemplate,
            final KafkaTemplate<String, LayoutLoadedEvent> kafkaLoadedTemplate,
            final KafkaTemplate<String, LayoutForkliftNumberChangedEvent> kafkaForkliftNumberTemplate
    ) {
        this.kafkaSavedTemplate = kafkaSavedTemplate;
        this.kafkaLoadedTemplate = kafkaLoadedTemplate;
        this.kafkaForkliftNumberTemplate = kafkaForkliftNumberTemplate;
    }

    public void sendLayoutSaved(final LayoutSavedEvent event) {
        kafkaSavedTemplate.send(KafkaTopics.LAYOUT_SAVED, event.layoutId().toString(), event);
        log.info("Sent LayoutSavedEvent for layoutId={}", event.layoutId());
    }

    public void sendLayoutLoaded(final LayoutLoadedEvent event) {
        kafkaLoadedTemplate.send(KafkaTopics.LAYOUT_LOADED, event.layoutId().toString(), event);
        log.info("Sent LayoutLoadedEvent for layoutId={}", event.layoutId());
    }

    public void sendForkliftNumberChanged(final LayoutForkliftNumberChangedEvent event) {
        kafkaForkliftNumberTemplate.send(KafkaTopics.LAYOUT_FORKLIFT_NUMBER_CHANGED, event.layoutId().toString(), event);
        log.info("Sent LayoutForkliftNumberChangedEvent for layoutId={}, forkliftNumber={}",
                event.layoutId(), event.forkliftNumber());
    }
}
