package io.swiczka.github.apiorder.producer;

import io.swiczka.github.sharedcommon.events.InboundOrderCreatedEvent;
import io.swiczka.github.sharedcommon.events.OutboundOrderCreatedEvent;
import io.swiczka.github.sharedcommon.topics.KafkaTopics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.kafka.core.KafkaTemplate;

@Component
public class OrderEventProducer {
    private final KafkaTemplate<String, InboundOrderCreatedEvent> inboundKafkaTemplate;
    private final KafkaTemplate<String, OutboundOrderCreatedEvent> outboundKafkaTemplate;
    private static final Logger log = LoggerFactory.getLogger(OrderEventProducer.class);

    @Autowired
    public OrderEventProducer(final KafkaTemplate<String, InboundOrderCreatedEvent> inboundKafkaTemplate,
                              final KafkaTemplate<String, OutboundOrderCreatedEvent> outboundKafkaTemplate) {
        this.inboundKafkaTemplate = inboundKafkaTemplate;
        this.outboundKafkaTemplate = outboundKafkaTemplate;
    }

    public void sendInboundOrderCreated(final InboundOrderCreatedEvent event){
        inboundKafkaTemplate.send(KafkaTopics.INBOUND_ORDER_CREATED, event.orderId().toString(), event)
                .whenComplete((result, ex) -> { if (ex != null) { log.error("Failed to send INBOUND_ORDER_CREATED", ex); } else { log.info("Sent INBOUND_ORDER_CREATED successfully"); } });
        log.info("Sent InboundOrderCreatedEvent for orderId={}", event.orderId());
    }

    public void sendOutboundOrderCreated(final OutboundOrderCreatedEvent event){
        outboundKafkaTemplate.send(KafkaTopics.OUTBOUND_ORDER_CREATED, event.orderId().toString(), event)
                .whenComplete((result, ex) -> { if (ex != null) { log.error("Failed to send OUTBOUND_ORDER_CREATED", ex); } else { log.info("Sent OUTBOUND_ORDER_CREATED successfully"); } });
        log.info("Sent OutboundOrderCreatedEvent for orderId={}", event.orderId());
    }
}
