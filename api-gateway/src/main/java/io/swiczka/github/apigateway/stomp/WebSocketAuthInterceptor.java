package io.swiczka.github.apigateway.stomp;

import io.swiczka.github.apigateway.client.WarehouseApiClient;
import io.swiczka.github.apigateway.helpers.GuestPrincipal;
import io.swiczka.github.sharedcommon.dto.LayoutOwnerDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private static final Logger log = LoggerFactory.getLogger(WebSocketAuthInterceptor.class);

    // matches /topic/layout/{layoutId}/... where layoutId is a number
    private static final Pattern LAYOUT_DESTINATION_PATTERN = Pattern.compile("^/topic/layout/(\\d+)/.*$");
    private static final String GUEST_ID_HEADER = "X-Guest-Id";

    private final WarehouseApiClient warehouseApiClient;

    @Autowired
    public WebSocketAuthInterceptor(final WarehouseApiClient warehouseApiClient) {
        this.warehouseApiClient = warehouseApiClient;
    }

    @Override
    public Message<?> preSend(final Message<?> message, final MessageChannel channel) {
        final StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) {
            return message;
        }

        try {
            if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                handleConnect(accessor);
                return message;
            }

            if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                handleSubscribe(accessor);
                return message;
            }
        } catch (final Exception e) {
            log.error("Exception in WebSocketAuthInterceptor preSend for command {}: {}",
                    accessor.getCommand(), e.getMessage(), e);
            throw e;
        }

        return message;
    }

    private void handleConnect(final StompHeaderAccessor accessor) {
        final String guestId = accessor.getFirstNativeHeader(GUEST_ID_HEADER);
        if (guestId == null || guestId.isBlank()) {
            log.warn("WebSocket CONNECT rejected: missing {} header", GUEST_ID_HEADER);
            throw new MessageDeliveryException("Missing " + GUEST_ID_HEADER + " header");
        }
        accessor.setUser(new GuestPrincipal(guestId));
        log.debug("WebSocket CONNECT accepted for guestId={}", guestId);
    }

    private void handleSubscribe(final StompHeaderAccessor accessor) {
        final String destination = accessor.getDestination();
        if (destination == null) {
            return;
        }

        final Long layoutId = parseLayoutId(destination);
        if (layoutId == null) {
            return;
        }

        final GuestPrincipal principal = (GuestPrincipal) accessor.getUser();
        if (principal == null) {
            log.warn("SUBSCRIBE rejected: no authenticated user for destination={}", destination);
            throw new MessageDeliveryException("Not authenticated");
        }

        final String guestId = principal.getName();
        log.info("Verifying subscription for guestId={}, destination={}, layoutId={}", guestId, destination, layoutId);

        final Optional<LayoutOwnerDto> owner = warehouseApiClient.getLayoutOwner(layoutId, guestId);
        log.info("Layout owner retrieved from client: {}", owner);

        final boolean isOwner = owner
                .filter(response -> response.userId() != null)
                .map(response -> response.userId().toString().equals(guestId))
                .orElse(false);

        if (!isOwner) {
            log.warn("SUBSCRIBE rejected: guestId={} is not the owner of layoutId={}", guestId, layoutId);
            throw new MessageDeliveryException("Access denied to layout " + layoutId);
        }

        log.info("SUBSCRIBE accepted: guestId={}, layoutId={}", guestId, layoutId);
    }

    private Long parseLayoutId(final String destination) {
        final Matcher matcher = LAYOUT_DESTINATION_PATTERN.matcher(destination);
        if (!matcher.matches()) {
            return null;
        }
        return Long.valueOf(matcher.group(1));
    }
}
