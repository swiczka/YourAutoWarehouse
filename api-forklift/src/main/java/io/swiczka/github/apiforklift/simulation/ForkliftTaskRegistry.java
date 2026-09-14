package io.swiczka.github.apiforklift.simulation;

import io.swiczka.github.apiforklift.domain.ForkliftTask;
import io.swiczka.github.apiforklift.dto.ForkliftTaskCreateDto;
import io.swiczka.github.apiforklift.enums.TaskStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class ForkliftTaskRegistry {
    private final ConcurrentHashMap<Long, ForkliftTask> taskMap;
    private final AtomicLong idSequence;


    public ForkliftTaskRegistry() {
        this.taskMap = new ConcurrentHashMap<>();
        this.idSequence = new AtomicLong(1);
    }

    public List<ForkliftTask> add(final List<ForkliftTaskCreateDto> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            return Collections.emptyList();
        }

        final List<ForkliftTask> tasks = new ArrayList<>();
        for (final ForkliftTaskCreateDto dto : dtos) {
            final Long newId = idSequence.getAndIncrement();
            final ForkliftTask task = new ForkliftTask(
                    newId,
                    dto.layoutId(),
                    dto.packageItemId(),
                    dto.sourceX(),
                    dto.sourceY(),
                    dto.targetX(),
                    dto.targetY(),
                    dto.taskType()
            );
            task.setStatus(TaskStatus.PENDING);
            this.taskMap.put(newId, task);
            tasks.add(task);
        }
        return tasks;
    }

    public ForkliftTask add(final ForkliftTaskCreateDto dto) {
        if (dto == null) {
            return null;
        }
        final List<ForkliftTask> tasks = add(List.of(dto));
        return tasks.isEmpty() ? null : tasks.getFirst();
    }

    public List<ForkliftTask> getByLayoutId(final Long layoutId) {
        if (layoutId == null) {
            return Collections.emptyList();
        }
        return this.taskMap.values().stream()
                .filter(task -> layoutId.equals(task.getLayoutId()))
                .toList();
    }

    public Optional<ForkliftTask> getById(final Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(this.taskMap.get(id));
    }

    public Collection<ForkliftTask> getAll() {
        return this.taskMap.values();
    }

    public Optional<ForkliftTask> remove(final Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(this.taskMap.remove(id));
    }
}
