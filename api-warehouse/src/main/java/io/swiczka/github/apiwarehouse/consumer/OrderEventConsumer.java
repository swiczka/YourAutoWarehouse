package io.swiczka.github.apiwarehouse.consumer;

import io.swiczka.github.apiwarehouse.packageitem.PackageItemService;
import io.swiczka.github.sharedcommon.events.InboundOrderCreatedEvent;
import io.swiczka.github.sharedcommon.topics.KafkaTopics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventConsumer {
    private static final Logger log = LoggerFactory.getLogger(OrderEventConsumer.class);
    private final PackageItemService packageService;


    @Autowired
    public OrderEventConsumer(PackageItemService packageService) {
        this.packageService = packageService;
    }

    @KafkaListener(topics = KafkaTopics.INBOUND_ORDER_CREATED, groupId = "warehouse-service")
    public void handleInboundOrderCreated(final InboundOrderCreatedEvent event) {
        log.info("Received InboundOrderCreatedEvent for orderId={}", event.orderId());
        packageService.addNewPackages(
                event.packageNames(),
                event.orderId(),
                event.layoutId()
        );
    }
}
