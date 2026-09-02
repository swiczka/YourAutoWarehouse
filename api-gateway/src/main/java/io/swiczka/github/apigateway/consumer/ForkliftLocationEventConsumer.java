package io.swiczka.github.apigateway.consumer;

import io.swiczka.github.apigateway.stomp.ForkliftWebsocketPublisher;
import io.swiczka.github.sharedcommon.events.ForkliftLocationEvent;
import io.swiczka.github.sharedcommon.topics.KafkaTopics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ForkliftLocationEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(ForkliftLocationEventConsumer.class);

    private final ForkliftWebsocketPublisher websocketPublisher;

    public ForkliftLocationEventConsumer(final ForkliftWebsocketPublisher websocketPublisher) {
        this.websocketPublisher = websocketPublisher;
    }

    @KafkaListener(topics = KafkaTopics.FORKLIFT_LOCATION_UPDATED, groupId = "gateway-service")
    public void handleForkliftLocationUpdated(final ForkliftLocationEvent event) {
        log.info("Received ForkliftLocationEvent for forkliftId={}", event.forkliftId());
        websocketPublisher.publishLocation(event);
    }
}
