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

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private static final Logger log = LoggerFactory.getLogger(WebSocketAuthInterceptor.class);

    // matches /topic/layout/{layoutId}/... where layoutId is a number
    private static final Pattern LAYOUT_DESTINATION_PATTERN = Pattern.compile("^/topic/layout/(\\d+)/.*$");
    private static final String GUEST_ID_HEADER = "X-Guest-Id";

    // layoutId -> set of active subscription keys ("sessionId:subscriptionId")
    private final Map<Long, Set<String>> layoutSubscribers = new ConcurrentHashMap<>();

    // subscriptionKey -> layoutId
    private final Map<String, Long> subscriptionLayouts = new ConcurrentHashMap<>();

    // sessionId -> set of subscriptionKeys belonging to this session
    private final Map<String, Set<String>> sessionSubscriptions = new ConcurrentHashMap<>();

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

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            handleConnect(accessor);
            return message;
        }

        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            handleSubscribe(accessor);
            return message;
        }

        if (StompCommand.UNSUBSCRIBE.equals(accessor.getCommand())) {
            handleUnsubscribe(accessor);
            return message;
        }

        if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            handleDisconnect(accessor);
            return message;
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
        final Optional<LayoutOwnerDto> owner = warehouseApiClient.getLayoutOwner(layoutId, guestId);

        final boolean isOwner = owner
                .map(response -> response.userId().toString().equals(guestId))
                .orElse(false);

        if (!isOwner) {
            log.warn("SUBSCRIBE rejected: guestId={} is not the owner of layoutId={}", guestId, layoutId);
            throw new MessageDeliveryException("Access denied to layout " + layoutId);
        }

        final String sessionId = accessor.getSessionId();
        final String subscriptionId = accessor.getSubscriptionId();
        if (sessionId == null || subscriptionId == null) {
            return;
        }

        final String subKey = buildSubscriptionKey(sessionId, subscriptionId);
        subscriptionLayouts.put(subKey, layoutId);

        sessionSubscriptions.computeIfAbsent(sessionId, ignored -> ConcurrentHashMap.newKeySet())
                .add(subKey);

        final Set<String> subscribers = layoutSubscribers.computeIfAbsent(
                layoutId, ignored -> ConcurrentHashMap.newKeySet()
        );

        final boolean isFirstSubscriber = subscribers.isEmpty();
        subscribers.add(subKey);

        if (isFirstSubscriber) {
            log.info("First subscriber for layoutId={}. Ready to notify forklift service to load layout.", layoutId);
            // TODO: In future, notify forklift service to load layout (e.g. via Kafka producer)
        }

        log.debug("SUBSCRIBE accepted: guestId={}, layoutId={}, subKey={}", guestId, layoutId, subKey);
    }

    private void handleUnsubscribe(final StompHeaderAccessor accessor) {
        final String sessionId = accessor.getSessionId();
        final String subscriptionId = accessor.getSubscriptionId();
        if (sessionId == null || subscriptionId == null) {
            return;
        }

        final String subKey = buildSubscriptionKey(sessionId, subscriptionId);
        removeSubscription(sessionId, subKey);
    }

    private void handleDisconnect(final StompHeaderAccessor accessor) {
        final String sessionId = accessor.getSessionId();
        if (sessionId == null) {
            return;
        }

        final Set<String> subKeys = sessionSubscriptions.remove(sessionId);
        if (subKeys == null || subKeys.isEmpty()) {
            return;
        }

        for (final String subKey : subKeys) {
            removeSubscriptionFromLayout(subKey);
        }
    }

    private void removeSubscription(final String sessionId, final String subKey) {
        final Set<String> sessionSubs = sessionSubscriptions.get(sessionId);
        if (sessionSubs != null) {
            sessionSubs.remove(subKey);
            if (sessionSubs.isEmpty()) {
                sessionSubscriptions.remove(sessionId);
            }
        }

        removeSubscriptionFromLayout(subKey);
    }

    private void removeSubscriptionFromLayout(final String subKey) {
        final Long layoutId = subscriptionLayouts.remove(subKey);
        if (layoutId == null) {
            return;
        }

        final Set<String> subscribers = layoutSubscribers.get(layoutId);
        if (subscribers == null) {
            return;
        }

        subscribers.remove(subKey);
        if (subscribers.isEmpty()) {
            layoutSubscribers.remove(layoutId);
            log.info("No more subscribers for layoutId={}. Ready to notify forklift service to unload layout.", layoutId);
            // TODO: In future, notify forklift service to unload layout after tasks complete
        }
    }

    private String buildSubscriptionKey(final String sessionId, final String subscriptionId) {
        return sessionId + ":" + subscriptionId;
    }

    private Long parseLayoutId(final String destination) {
        final Matcher matcher = LAYOUT_DESTINATION_PATTERN.matcher(destination);
        if (!matcher.matches()) {
            return null;
        }
        return Long.valueOf(matcher.group(1));
    }
}
