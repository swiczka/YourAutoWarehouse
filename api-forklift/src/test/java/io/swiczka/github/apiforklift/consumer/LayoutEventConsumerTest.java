package io.swiczka.github.apiforklift.consumer;

import io.swiczka.github.apiforklift.domain.SimulationLayout;
import io.swiczka.github.apiforklift.simulation.LayoutCache;
import io.swiczka.github.sharedcommon.events.LayoutForkliftNumberChangedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class LayoutEventConsumerTest {

    private static final Long LAYOUT_ID = 10L;

    private LayoutCache layoutCache;
    private LayoutEventConsumer consumer;

    @BeforeEach
    void setUp() {
        layoutCache = new LayoutCache();
        consumer = new LayoutEventConsumer(layoutCache);
    }

    @Test
    @DisplayName("should update forklift number of a simulated layout")
    void handleForkliftNumberChanged_updatesLayout() {
        // given
        final SimulationLayout layout = new SimulationLayout(LAYOUT_ID, UUID.randomUUID(), 5, Collections.emptyList());
        layoutCache.put(layout);

        // when
        consumer.handleForkliftNumberChanged(new LayoutForkliftNumberChangedEvent(LAYOUT_ID, 8));

        // then
        assertThat(layoutCache.get(LAYOUT_ID)).get()
                .extracting(SimulationLayout::getForkliftNumber)
                .isEqualTo(8);
    }

    @Test
    @DisplayName("should ignore layouts that are not simulated")
    void handleForkliftNumberChanged_unknownLayout() {
        // given - empty cache

        // when then
        assertThatCode(() -> consumer.handleForkliftNumberChanged(new LayoutForkliftNumberChangedEvent(LAYOUT_ID, 3)))
                .doesNotThrowAnyException();
        assertThat(layoutCache.contains(LAYOUT_ID)).isFalse();
    }
}
