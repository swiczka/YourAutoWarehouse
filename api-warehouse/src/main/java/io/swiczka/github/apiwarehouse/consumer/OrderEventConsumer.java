package io.swiczka.github.apiwarehouse.consumer;

import io.swiczka.github.sharedcommon.events.LayoutSavedEvent;
import io.swiczka.github.sharedcommon.events.OrderCreatedEvent;
import io.swiczka.github.sharedcommon.topics.KafkaTopics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventConsumer {
    private static final Logger log = LoggerFactory.getLogger(OrderEventConsumer.class);

    @Autowired
    public OrderEventConsumer() {

    }

    @KafkaListener(topics = KafkaTopics.ORDER_CREATED, groupId = "warehouse-service")
    public void handleOrderCreated(final OrderCreatedEvent event) {
        log.info("Received OrderCreatedEvent for orderId={}", event.orderId());
    }
}
