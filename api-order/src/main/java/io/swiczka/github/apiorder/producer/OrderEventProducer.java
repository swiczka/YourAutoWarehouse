package io.swiczka.github.apiorder.producer;

import io.swiczka.github.sharedcommon.events.InboundOrderCreatedEvent;
import io.swiczka.github.sharedcommon.topics.KafkaTopics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.kafka.core.KafkaTemplate;

@Component
public class OrderEventProducer {
    private final KafkaTemplate<String, InboundOrderCreatedEvent> kafkaTemplate;
    private static final Logger log = LoggerFactory.getLogger(OrderEventProducer.class);

    @Autowired
    public OrderEventProducer(KafkaTemplate<String, InboundOrderCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;

    }

    public void sendInboundOrderCreated(final InboundOrderCreatedEvent event){
        kafkaTemplate.send(KafkaTopics.INBOUND_ORDER_CREATED, event.orderId().toString(), event)
                .whenComplete((result, ex) -> { if (ex != null) { log.error("Failed to send", ex); } else { log.info("Sent successfully"); } });
        log.info("Sent InboundOrderCreatedEvent for orderId={}", event.orderId());
    }
}
