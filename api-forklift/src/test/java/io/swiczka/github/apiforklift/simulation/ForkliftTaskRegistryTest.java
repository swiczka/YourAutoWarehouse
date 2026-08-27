package io.swiczka.github.apiforklift.simulation;

import io.swiczka.github.apiforklift.domain.ForkliftTask;
import io.swiczka.github.apiforklift.dto.ForkliftTaskCreateDto;
import io.swiczka.github.apiforklift.enums.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ForkliftTaskRegistryTest {

    private ForkliftTaskRegistry taskRegistry;

    @BeforeEach
    void setUp() {
        taskRegistry = new ForkliftTaskRegistry();
    }

    @Test
    @DisplayName("should add tasks and retrieve them by layoutId")
    void shouldAddAndGetByLayoutId() {
        // given
        final Long layout1 = 10L;
        final Long layout2 = 20L;

        final List<ForkliftTaskCreateDto> dtos = List.of(
                new ForkliftTaskCreateDto(1L, layout1, 0, 0, 5, 5),
                new ForkliftTaskCreateDto(2L, layout1, 0, 0, 6, 6),
                new ForkliftTaskCreateDto(3L, layout2, 0, 0, 7, 7)
        );

        // when
        final List<ForkliftTask> created = taskRegistry.add(dtos);

        // then
        assertThat(created).hasSize(3);
        assertThat(created).allMatch(t -> t.getStatus() == TaskStatus.PENDING);

        final List<ForkliftTask> layout1Tasks = taskRegistry.getByLayoutId(layout1);
        assertThat(layout1Tasks).hasSize(2);
        assertThat(layout1Tasks).allMatch(t -> t.getLayoutId().equals(layout1));

        final List<ForkliftTask> layout2Tasks = taskRegistry.getByLayoutId(layout2);
        assertThat(layout2Tasks).hasSize(1);
        assertThat(layout2Tasks.getFirst().getLayoutId()).isEqualTo(layout2);
    }

    @Test
    @DisplayName("should return empty list when no tasks exist for layoutId or when null passed")
    void shouldReturnEmptyListWhenNotFound() {
        // when
        final List<ForkliftTask> nonExistent = taskRegistry.getByLayoutId(999L);
        final List<ForkliftTask> nullLayout = taskRegistry.getByLayoutId(null);

        // then
        assertThat(nonExistent).isEmpty();
        assertThat(nullLayout).isEmpty();
    }

    @Test
    @DisplayName("should get by id and remove task")
    void shouldGetByIdAndRemove() {
        // given
        final List<ForkliftTaskCreateDto> dtos = List.of(
                new ForkliftTaskCreateDto(100L, 5L, 0, 0, 1, 1)
        );
        final List<ForkliftTask> added = taskRegistry.add(dtos);
        final Long taskId = added.getFirst().getTaskId();

        // when & then
        final Optional<ForkliftTask> found = taskRegistry.getById(taskId);
        assertThat(found).isPresent();
        assertThat(found.get().getPackageId()).isEqualTo(100L);

        final Optional<ForkliftTask> removed = taskRegistry.remove(taskId);
        assertThat(removed).isPresent();
        assertThat(taskRegistry.getById(taskId)).isEmpty();
    }
}
