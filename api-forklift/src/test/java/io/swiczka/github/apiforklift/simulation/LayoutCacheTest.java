package io.swiczka.github.apiforklift.simulation;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swiczka.github.apiforklift.domain.SimulationLayout;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class LayoutCacheTest {

    private LayoutCache layoutCache;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        layoutCache = new LayoutCache(objectMapper);
    }

    @Test
    @DisplayName("should automatically load sample layout on init (@PostConstruct)")
    void shouldLoadSampleLayoutOnInit() {
        // when
        layoutCache.init();

        // then
        assertThat(layoutCache.contains(24L)).isTrue();
        final Optional<SimulationLayout> sampleLayout = layoutCache.get(24L);
        assertThat(sampleLayout).isPresent();
        assertThat(sampleLayout.get().getForkliftNumber()).isEqualTo(5);
    }

    @Test
    @DisplayName("should put, get and remove layout from cache")
    void shouldManageLayoutInCache() {
        // given
        final Long layoutId = 999L;
        final SimulationLayout layout = new SimulationLayout(layoutId, UUID.randomUUID(), 5, Collections.emptyList());

        // when
        layoutCache.put(layout);

        // then
        assertThat(layoutCache.contains(layoutId)).isTrue();
        final Optional<SimulationLayout> cached = layoutCache.get(layoutId);
        assertThat(cached).isPresent();
        assertThat(cached.get().getId()).isEqualTo(layoutId);

        // when remove
        final Optional<SimulationLayout> removed = layoutCache.remove(layoutId);

        // then
        assertThat(removed).isPresent();
        assertThat(layoutCache.contains(layoutId)).isFalse();
        assertThat(layoutCache.get(layoutId)).isEmpty();
    }
}
