package io.swiczka.github.apiforklift.simulation;

import io.swiczka.github.apiforklift.domain.Forklift;
import io.swiczka.github.apiforklift.domain.ForkliftTask;
import io.swiczka.github.apiforklift.domain.SimulationLayout;
import io.swiczka.github.apiforklift.dto.ForkliftCreateDto;
import io.swiczka.github.apiforklift.enums.ForkliftStatus;
import io.swiczka.github.apiforklift.enums.TaskStatus;
import io.swiczka.github.apiforklift.producer.ForkliftLocationEventProducer;
import io.swiczka.github.sharedcommon.events.ForkliftLocationEvent;
import io.swiczka.github.sharedcommon.helpers.Coordinate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
public class SimulationEngine {

    private static final Logger log = LoggerFactory.getLogger(SimulationEngine.class);

    private final ForkliftRegistry forkliftRegistry;
    private final LayoutCache layoutCache;
    private final ForkliftTaskRegistry taskRegistry;
    private final ForkliftLocationEventProducer locationEventProducer;

    @Autowired
    public SimulationEngine(final ForkliftRegistry forkliftRegistry,
                            final LayoutCache layoutCache,
                            final ForkliftTaskRegistry taskRegistry,
                            final ForkliftLocationEventProducer locationEventProducer) {
        this.forkliftRegistry = forkliftRegistry;
        this.layoutCache = layoutCache;
        this.taskRegistry = taskRegistry;
        this.locationEventProducer = locationEventProducer;
    }

    @Scheduled(fixedRate = 500)
    public void tick() {
        for (final SimulationLayout layout : layoutCache.getAll()) {
            ensureForkliftsCreated(layout);
            assignPendingTasks(layout);
            stepForklifts(layout);
        }
    }

    private void assignPendingTasks(final SimulationLayout layout) {
        final List<ForkliftTask> pendingTasks = taskRegistry.getByLayoutId(layout.getId()).stream()
                .filter(task -> task.getStatus().equals(TaskStatus.PENDING))
                .toList();

        for (final ForkliftTask task : pendingTasks) {
            final List<Forklift> freeForklifts = forkliftRegistry.findFreeByLayoutId(layout.getId());
            if (freeForklifts.isEmpty()) {
                break;
            }

            final Forklift forklift = freeForklifts.getFirst();
            assignTaskToForklift(forklift, task, layout);
        }
    }

    private void assignTaskToForklift(
            final Forklift forklift,
            final ForkliftTask task,
            final SimulationLayout layout
    ) {
        final Coordinate start = new Coordinate(forklift.getX(), forklift.getY());
        final Coordinate source = new Coordinate(task.getSourceX(), task.getSourceY());

        final List<Coordinate> pathToSource = AStar.findPath(start, source, layout);
        if (pathToSource.isEmpty() && !start.equals(source)) {
            log.warn("Cannot find path for forklift {} to task source {}", forklift.getId(), source);
            return;
        }

        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setAssignedForkliftId(forklift.getId());

        forklift.setCurrentTaskId(task.getTaskId());
        forklift.setStatus(ForkliftStatus.MOVING);

        final List<Coordinate> steps = pathToSource.size() > 1
                ? pathToSource.subList(1, pathToSource.size())
                : pathToSource;
        forklift.setPath(steps);

        log.info("Assigned task {} to forklift {}. Path length: {}", task.getTaskId(), forklift.getId(), steps.size());
    }

    private void stepForklifts(final SimulationLayout layout) {
        final List<Forklift> forklifts = forkliftRegistry.findByLayoutId(layout.getId());

        for (final Forklift forklift : forklifts) {
            if (!forklift.hasPath()) {
                continue;
            }

            forklift.nextStep();
            log.info("Forklift {} reached {}, {}",
                    forklift.getId(),
                    forklift.getX(),
                    forklift.getY());

            ForkliftLocationEvent event = new ForkliftLocationEvent(
                    forklift.getId(),
                    layout.getId(),
                    forklift.getX(),
                    forklift.getY(),
                    Instant.now()
            );
            locationEventProducer.sendNewLocation(event);

            if (!forklift.hasPath()) {
                handleDestinationReached(forklift, layout);
            }
        }
    }

    private void handleDestinationReached(final Forklift forklift, final SimulationLayout layout) {
        final Long taskId = forklift.getCurrentTaskId();
        if (taskId == null) {
            if (forklift.getStatus().equals(ForkliftStatus.RETURNING)) {
                forklift.setStatus(ForkliftStatus.IN_GARAGE);
            }
            return;
        }

        final Optional<ForkliftTask> taskOpt = taskRegistry.getById(taskId);
        if (taskOpt.isEmpty()) {
            log.warn("Forklift {} has assigned task {} but id does not exist!", forklift.getId(), taskId);
            return;
        }
        final ForkliftTask task = taskOpt.get();

        // arrived to the package, picking up the package
        if (forklift.getCurrentPackageId() == null && forklift.getStatus().equals(ForkliftStatus.MOVING)) {
            forklift.setCurrentPackageId(task.getPackageId());
            forklift.setStatus(ForkliftStatus.CARRYING);

            final Coordinate source = new Coordinate(forklift.getX(), forklift.getY());
            final Coordinate target = new Coordinate(task.getTargetX(), task.getTargetY());
            final List<Coordinate> pathToTarget = AStar.findPath(source, target, layout);

            final List<Coordinate> steps = pathToTarget.size() > 1
                    ? pathToTarget.subList(1, pathToTarget.size())
                    : pathToTarget;
            forklift.setPath(steps);
            log.info("Forklift {} picked up package {}. Heading to target {}", forklift.getId(), task.getPackageId(), target);
            return;
        }

        // forklift carried package to its destination
        log.info("Forklift {} delivered package {} to target ({}, {})",
                forklift.getId(), task.getPackageId(), task.getTargetX(), task.getTargetY());

        forklift.setCurrentPackageId(null);
        forklift.setCurrentTaskId(null);
        task.setStatus(TaskStatus.COMPLETE);

        //TODO - here we can communicate about new package location

        // return to the garage
        final Coordinate current = new Coordinate(forklift.getX(), forklift.getY());
        final Coordinate garage = new Coordinate(0, 0);
        final List<Coordinate> pathToGarage = AStar.findPath(current, garage, layout);

        if (!pathToGarage.isEmpty() && !current.equals(garage)) {
            final List<Coordinate> steps = pathToGarage.size() > 1
                    ? pathToGarage.subList(1, pathToGarage.size())
                    : pathToGarage;
            forklift.setStatus(ForkliftStatus.RETURNING);
            forklift.setPath(steps);
            log.info("Forklift {} is returning to the garage!", forklift.getId());
        } else {
            forklift.setStatus(ForkliftStatus.IN_GARAGE);
            log.info("Forklift {} returned to the garage!", forklift.getId());
        }
    }

    private void ensureForkliftsCreated(final SimulationLayout layout) {
        final List<Forklift> existingForklifts = forkliftRegistry.findByLayoutId(layout.getId());
        final int targetCount = layout.getForkliftNumber() != null ? layout.getForkliftNumber() : 0;
        final int missingCount = targetCount - existingForklifts.size();

        if (missingCount > 0) {
            log.info("Creating {} missing forklift(s) for layout id={}", missingCount, layout.getId());
            for (int i = 0; i < missingCount; i++) {
                final ForkliftCreateDto createDto = new ForkliftCreateDto(layout.getId(), layout.getUserId());
                forkliftRegistry.add(createDto);
            }
        }
    }
}
