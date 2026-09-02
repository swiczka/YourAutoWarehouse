package io.swiczka.github.apiorder.producer;

import io.swiczka.github.sharedcommon.topics.KafkaTopics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.kafka.core.KafkaTemplate;
import io.swiczka.github.sharedcommon.events.OrderCreatedEvent;

@Component
public class OrderEventProducer {
    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;
    private static final Logger log = LoggerFactory.getLogger(OrderEventProducer.class);

    @Autowired
    public OrderEventProducer(KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;

    }

    public void sendOrderCreated(final OrderCreatedEvent event){
        kafkaTemplate.send(KafkaTopics.ORDER_CREATED, event.orderId().toString(), event)
                .whenComplete((result, ex) -> { if (ex != null) { log.error("Failed to send", ex); } else { log.info("Sent successfully"); } }); ;
        log.info("Sent OrderCreatedEvent for orderId={}", event.orderId());
    }
}
