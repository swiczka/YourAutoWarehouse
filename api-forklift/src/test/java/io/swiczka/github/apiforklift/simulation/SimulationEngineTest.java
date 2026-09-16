package io.swiczka.github.apiforklift.simulation;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swiczka.github.apiforklift.domain.Forklift;
import io.swiczka.github.apiforklift.domain.ForkliftTask;
import io.swiczka.github.apiforklift.domain.SimulationLayout;
import io.swiczka.github.apiforklift.dto.ForkliftTaskCreateDto;
import io.swiczka.github.apiforklift.enums.TaskStatus;
import io.swiczka.github.sharedcommon.helpers.TaskType;
import io.swiczka.github.apiforklift.producer.ForkliftLocationEventProducer;
import io.swiczka.github.apiforklift.producer.PackageDroppedEventProducer;
import io.swiczka.github.apiforklift.producer.PackagePickedEventProducer;
import io.swiczka.github.apiforklift.producer.PackageStoredEventProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class SimulationEngineTest {

    private ForkliftRegistry forkliftRegistry;
    private LayoutCache layoutCache;
    private ForkliftTaskRegistry taskRegistry;
    private SimulationEngine simulationEngine;

    @BeforeEach
    void setUp() {
        forkliftRegistry = new ForkliftRegistry();
        layoutCache = new LayoutCache();
        taskRegistry = new ForkliftTaskRegistry();
        PackageStoredEventProducer packageStoredEventProducer = mock(PackageStoredEventProducer.class);
        ForkliftLocationEventProducer locationEventProducer = mock(ForkliftLocationEventProducer.class);
        PackagePickedEventProducer packagePickedEventProducer = mock(PackagePickedEventProducer.class);
        PackageDroppedEventProducer packageDroppedEventProducer = mock(PackageDroppedEventProducer.class);

        simulationEngine = new SimulationEngine(
                forkliftRegistry,
                layoutCache,
                taskRegistry,
                locationEventProducer,
                packageStoredEventProducer,
                packagePickedEventProducer,
                packageDroppedEventProducer
        );
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
    }

    @Test
    @DisplayName("should assign pending task to free forklift and step through path")
    void shouldAssignPendingTaskAndMoveForklift() throws Exception {
        // given - load real sample layout
        final ObjectMapper mapper = new ObjectMapper();
        final ClassPathResource resource = new ClassPathResource("data/sample_layout.json");
        final SimulationLayout layout;
        try (final java.io.InputStream is = resource.getInputStream()) {
            layout = mapper.readValue(is, SimulationLayout.class);
        }
        layoutCache.put(layout);

        // create a task on sample layout: source (0, 1), target (2, 0)
        final ForkliftTaskCreateDto taskDto =
                new ForkliftTaskCreateDto(555L, layout.getId(), 0, 1, 2, 0, TaskType.INBOUND);
        final List<ForkliftTask> addedTasks = taskRegistry.add(List.of(taskDto));
        final ForkliftTask task = addedTasks.getFirst();

        // when 1: first tick creates forklifts and assigns pending task
        simulationEngine.tick();

        // then
        assertThat(task.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
        assertThat(task.getAssignedForkliftId()).isNotNull();

        final Forklift assignedForklift = forkliftRegistry.getById(task.getAssignedForkliftId()).orElseThrow();
        assertThat(assignedForklift.getCurrentTaskId()).isEqualTo(task.getTaskId());

        // when 2: run ticks until task is completed
        for (int i = 0; i < 20; i++) {
            simulationEngine.tick();
            if (task.getStatus() == TaskStatus.COMPLETE) {
                break;
            }
        }

        // then
        assertThat(task.getStatus()).isEqualTo(TaskStatus.COMPLETE);
    }
}
