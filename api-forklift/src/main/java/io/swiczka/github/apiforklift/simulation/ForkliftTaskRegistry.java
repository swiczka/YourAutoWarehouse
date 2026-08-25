package io.swiczka.github.apiforklift.simulation;

import io.swiczka.github.apiforklift.domain.ForkliftTask;
import io.swiczka.github.apiforklift.dto.ForkliftTaskCreateDto;
import io.swiczka.github.apiforklift.enums.TaskStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class ForkliftTaskRegistry {
    private final ConcurrentHashMap<Long, ForkliftTask> taskMap;

    private final AtomicLong idSequence;

    public ForkliftTaskRegistry() {
        this.taskMap = new ConcurrentHashMap<Long, ForkliftTask>();
        this.idSequence = new AtomicLong();
    }

    public List<ForkliftTask> add(List<ForkliftTaskCreateDto> dtos){
        List<ForkliftTask> tasks = new ArrayList<>();
        for(ForkliftTaskCreateDto dto : dtos){
            Long newId = idSequence.getAndIncrement();
            ForkliftTask task = new ForkliftTask(
                    newId,
                    dto.packageItemId(),
                    dto.sourceX(),
                    dto.sourceY(),
                    dto.targetX(),
                    dto.targetY()
            );
            task.setStatus(TaskStatus.PENDING);
            this.taskMap.put(newId, task);
            tasks.add(task);
        }
        return tasks;
    }
}
