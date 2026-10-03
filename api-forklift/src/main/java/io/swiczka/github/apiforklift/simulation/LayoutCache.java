package io.swiczka.github.apiforklift.simulation;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swiczka.github.apiforklift.domain.SimulationLayout;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LayoutCache {

    private static final Logger log = LoggerFactory.getLogger(LayoutCache.class);
    private static final String SAMPLE_LAYOUT_RESOURCE = "data/sample_layout.json";

    private final ConcurrentHashMap<Long, SimulationLayout> activeLayouts = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    public LayoutCache() {
        this.objectMapper = new ObjectMapper();
    }

    public LayoutCache(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper != null ? objectMapper : new ObjectMapper();
    }

    @PostConstruct
    public void init() {
        loadSampleLayout();
    }

    private void loadSampleLayout() {
        try {
            final ClassPathResource resource = new ClassPathResource(SAMPLE_LAYOUT_RESOURCE);
            if (resource.exists()) {
                try (final InputStream inputStream = resource.getInputStream()) {
                    final SimulationLayout sampleLayout = objectMapper.readValue(inputStream, SimulationLayout.class);
                    put(sampleLayout);
                    log.info("Successfully loaded sample layout into LayoutCache with id={}", sampleLayout.getId());
                }
            } else {
                log.warn("Sample layout resource not found at {}", SAMPLE_LAYOUT_RESOURCE);
            }
        } catch (final Exception e) {
            log.error("Failed to load sample layout from resource {}", SAMPLE_LAYOUT_RESOURCE, e);
        }
    }

    public void put(final SimulationLayout layout) {
        if (layout != null && layout.getId() != null) {
            layout.touch();
            activeLayouts.put(layout.getId(), layout);
        }
    }

    public Optional<SimulationLayout> get(final Long layoutId) {
        if (layoutId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(activeLayouts.get(layoutId));
    }

    public void touch(final Long layoutId) {
        if (layoutId == null) {
            return;
        }
        final SimulationLayout layout = activeLayouts.get(layoutId);
        if (layout != null) {
            layout.touch();
        }
    }

    public void markOldLayoutsForUserAsRemovable(final UUID userId, final Long newLayoutId) {
        if (userId == null) {
            return;
        }
        for (final SimulationLayout layout : activeLayouts.values()) {
            final boolean isSameUser = userId.equals(layout.getUserId());
            final boolean isDifferentLayout = newLayoutId == null || !newLayoutId.equals(layout.getId());
            if (isSameUser && isDifferentLayout) {
                layout.markForRemoval();
                log.info("Marked previous layout id={} for user={} as removable", layout.getId(), userId);
            }
        }
    }

    public Optional<SimulationLayout> remove(final Long layoutId) {
        if (layoutId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(activeLayouts.remove(layoutId));
    }

    public Collection<SimulationLayout> getAll() {
        return activeLayouts.values();
    }

    public boolean contains(final Long layoutId) {
        if (layoutId == null) {
            return false;
        }
        return activeLayouts.containsKey(layoutId);
    }
}
