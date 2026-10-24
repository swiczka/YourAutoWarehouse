package io.swiczka.github.apigateway.exceptions;

import org.springframework.messaging.MessageDeliveryException;

public class StompRequestRejectedException extends MessageDeliveryException {

    public StompRequestRejectedException(final String description) {
        super(description);
    }
}
