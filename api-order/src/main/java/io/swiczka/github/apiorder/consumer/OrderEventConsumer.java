package io.swiczka.github.apiorder.consumer;

import io.swiczka.github.apiorder.OrderService;
import io.swiczka.github.sharedcommon.events.OrderCompleteEvent;
import io.swiczka.github.sharedcommon.topics.KafkaTopics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventConsumer {
    private static final Logger log = LoggerFactory.getLogger(OrderEventConsumer.class);
    private final OrderService orderService;

    @Autowired
    public OrderEventConsumer(OrderService orderService) {
        this.orderService = orderService;
    }

    @KafkaListener(topics = KafkaTopics.ORDER_COMPLETE, groupId = "order-service")
    public void handleOrderComplete(final OrderCompleteEvent event) {
        log.info("Received OrderCompleteEvent for {} order Id={}", event.taskType(), event.orderId());
        orderService.handleOrderComplete(event.orderId(), event.taskType());
    }
}
