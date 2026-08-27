package io.swiczka.github.apiforklift.simulation;

import io.swiczka.github.apiforklift.domain.Forklift;
import io.swiczka.github.apiforklift.domain.SimulationLayout;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SimulationEngineTest {

    private ForkliftRegistry forkliftRegistry;
    private LayoutCache layoutCache;
    private SimulationEngine simulationEngine;
    private ForkliftTaskRegistry taskRegistry;

    @BeforeEach
    void setUp() {
        forkliftRegistry = new ForkliftRegistry();
        layoutCache = new LayoutCache();
        taskRegistry = new ForkliftTaskRegistry();

        simulationEngine = new SimulationEngine(forkliftRegistry, layoutCache, taskRegistry);
    }

    @Test
    @DisplayName("should automatically create missing forklifts for active layouts on tick")
    void shouldCreateMissingForkliftsOnTick() {
        // given
        final Long layoutId = 100L;
        final UUID userId = UUID.randomUUID();
        final int targetForklifts = 3;
        final SimulationLayout layout = new SimulationLayout(layoutId, userId, targetForklifts, Collections.emptyList());
        layoutCache.put(layout);

        // when
        simulationEngine.tick();

        // then
        final List<Forklift> forklifts = forkliftRegistry.findByLayoutId(layoutId);
        assertThat(forklifts).hasSize(targetForklifts);
        assertThat(forklifts).allMatch(f -> f.getLayoutId().equals(layoutId));
        assertThat(forklifts).allMatch(f -> f.getOperatorId().equals(userId));

        // when another tick occurs
        simulationEngine.tick();

        // then count does not increase beyond target
        assertThat(forkliftRegistry.findByLayoutId(layoutId)).hasSize(targetForklifts);
    }
}
