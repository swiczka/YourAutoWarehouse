package io.swiczka.github.apigateway.stomp;

import io.swiczka.github.sharedcommon.events.ForkliftLocationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class ForkliftWebsocketPublisher {

    private final SimpMessagingTemplate messagingTemplate;
    private static final Logger log = LoggerFactory.getLogger(ForkliftWebsocketPublisher.class);

    @Autowired
    public ForkliftWebsocketPublisher(final SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }


    public void publishLocation(final ForkliftLocationEvent event) {
        final String destination = "/topic/layout/" + event.layoutId() + "/forklifts";
        log.debug("Publishing forklift location to destination: {}", destination);
        messagingTemplate.convertAndSend(destination, event);
    }
}
