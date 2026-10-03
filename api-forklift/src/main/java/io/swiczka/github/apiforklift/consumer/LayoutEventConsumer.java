package io.swiczka.github.apiforklift.consumer;

import io.swiczka.github.apiforklift.domain.SimulationLayout;
import io.swiczka.github.apiforklift.mapper.SimulationLayoutMapper;
import io.swiczka.github.apiforklift.simulation.LayoutCache;
import io.swiczka.github.sharedcommon.events.LayoutLoadedEvent;
import io.swiczka.github.sharedcommon.events.LayoutSavedEvent;
import io.swiczka.github.sharedcommon.topics.KafkaTopics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class LayoutEventConsumer {
    private final LayoutCache layoutCache;
    private static final Logger log = LoggerFactory.getLogger(LayoutEventConsumer.class);

    @Autowired
    public LayoutEventConsumer(final LayoutCache layoutCache) {
        this.layoutCache = layoutCache;
    }

    @KafkaListener(topics = KafkaTopics.LAYOUT_SAVED, groupId = "forklift-service")
    public void handleLayoutSaved(final LayoutSavedEvent event) {
        log.info("Received LayoutSavedEvent for layoutId={}", event.layoutId());
        layoutCache.markOldLayoutsForUserAsRemovable(event.userId(), event.layoutId());
        final SimulationLayout layout = SimulationLayoutMapper.toSimulation(event);
        layoutCache.put(layout);
    }

    @KafkaListener(topics = KafkaTopics.LAYOUT_LOADED, groupId = "forklift-service")
    public void handleLayoutLoaded(final LayoutLoadedEvent event) {
        log.info("Received LayoutLoadedEvent for layoutId={}", event.layoutId());
        if (layoutCache.contains(event.layoutId())) {
            layoutCache.touch(event.layoutId());
            return;
        }

        final SimulationLayout layout = SimulationLayoutMapper.toSimulation(event);
        layoutCache.put(layout);
    }
}
