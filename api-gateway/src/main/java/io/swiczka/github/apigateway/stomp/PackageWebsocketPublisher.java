package io.swiczka.github.apigateway.stomp;

import io.swiczka.github.sharedcommon.events.PackageAllocatedEvent;
import io.swiczka.github.sharedcommon.events.PackagePickedEvent;
import io.swiczka.github.sharedcommon.events.PackageStoredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class PackageWebsocketPublisher {
    private final SimpMessagingTemplate messagingTemplate;
    private static final Logger log = LoggerFactory.getLogger(PackageWebsocketPublisher.class);

    @Autowired
    public PackageWebsocketPublisher(final SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void publishLocation(final PackageStoredEvent event) {
        final String destination = "/topic/layout/" + event.layoutId() + "/packages";
        log.debug("Publishing package location to destination: {}", destination);
        messagingTemplate.convertAndSend(destination, event);
    }

    public void publishAllocated(final PackageAllocatedEvent event) {
        final String destination = "/topic/layout/" + event.layoutId() + "/packages";
        log.debug("Publishing package allocated to destination: {}", destination);
        messagingTemplate.convertAndSend(destination, event);
    }

    public void publishPicked(final PackagePickedEvent event) {
        final String destination = "/topic/layout/" + event.layoutId() + "/packages";
        log.debug("Publishing package picked to destination: {}", destination);
        messagingTemplate.convertAndSend(destination, event);
    }
}
