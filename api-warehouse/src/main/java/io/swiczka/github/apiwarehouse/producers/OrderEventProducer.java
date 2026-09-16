package io.swiczka.github.apiwarehouse.producers;

import io.swiczka.github.sharedcommon.events.OrderCompleteEvent;
import io.swiczka.github.sharedcommon.helpers.TaskType;
import io.swiczka.github.sharedcommon.topics.KafkaTopics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventProducer {
    private final KafkaTemplate<String, OrderCompleteEvent> kafkaTemplate;
    private static final Logger log = LoggerFactory.getLogger(OrderEventProducer.class);

    @Autowired
    public OrderEventProducer(final KafkaTemplate<String, OrderCompleteEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOrderComplete(final OrderCompleteEvent event){
        final String taskTypeMarker = event.orderId().toString() +
                                    (event.taskType() == TaskType.INBOUND ? "I" : "O");
        kafkaTemplate.send(KafkaTopics.ORDER_COMPLETE, taskTypeMarker, event);
        log.info("Sent OrderCompleteEvent for orderId={}", event.orderId());
    }
}
