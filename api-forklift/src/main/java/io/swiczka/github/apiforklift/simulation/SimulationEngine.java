package io.swiczka.github.apiforklift.simulation;

import io.swiczka.github.apiforklift.domain.Forklift;
import io.swiczka.github.apiforklift.domain.ForkliftTask;
import io.swiczka.github.apiforklift.domain.SimulationLayout;
import io.swiczka.github.apiforklift.dto.ForkliftCreateDto;
import io.swiczka.github.apiforklift.enums.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SimulationEngine {

    private static final Logger log = LoggerFactory.getLogger(SimulationEngine.class);

    private final ForkliftRegistry forkliftRegistry;
    private final LayoutCache layoutCache;
    private final ForkliftTaskRegistry taskRegistry;

    @Autowired
    public SimulationEngine(final ForkliftRegistry forkliftRegistry,
                            final LayoutCache layoutCache,
                            final ForkliftTaskRegistry taskRegistry) {
        this.forkliftRegistry = forkliftRegistry;
        this.layoutCache = layoutCache;
        this.taskRegistry = taskRegistry;
    }

    @Scheduled(fixedRate = 500)
    public void tick() {
        for (final SimulationLayout layout : layoutCache.getAll()) {
            ensureForkliftsCreated(layout);
            final List<ForkliftTask> tasks = taskRegistry.getByLayoutId(layout.getId());
            for (final ForkliftTask task : tasks){
                if(task.getStatus().equals(TaskStatus.PENDING)){
                    //means that order needs to be assigned
                }
                //... idk yet
            }
        }
    }

    private void ensureForkliftsCreated(final SimulationLayout layout) {
        final List<Forklift> existingForklifts = forkliftRegistry.findByLayoutId(layout.getId());
        final int targetCount = layout.getForkliftNumber() != null ? layout.getForkliftNumber() : 0;
        final int missingCount = targetCount - existingForklifts.size();

        if (missingCount > 0) {
            log.info("Creating {} missing forklift(s) for layout id={}", missingCount, layout.getId());
            for (int i = 0; i < missingCount; i++) {
                //TODO: DTO is confusing here but it can stay that way for now
                final ForkliftCreateDto createDto = new ForkliftCreateDto(layout.getId(), layout.getUserId());
                forkliftRegistry.add(createDto);
            }
        }
    }
}
