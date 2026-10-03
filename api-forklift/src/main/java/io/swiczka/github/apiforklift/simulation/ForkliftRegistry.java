package io.swiczka.github.apiforklift.simulation;

import io.swiczka.github.apiforklift.domain.Forklift;
import io.swiczka.github.apiforklift.dto.ForkliftCreateDto;
import io.swiczka.github.apiforklift.enums.ForkliftStatus;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class ForkliftRegistry {
    private final ConcurrentHashMap<Long, Forklift> forkliftsMap;
    private final AtomicLong idSequence;

    public ForkliftRegistry() {
        this.forkliftsMap = new ConcurrentHashMap<>();
        this.idSequence = new AtomicLong(1);
    }

    public Forklift add(final ForkliftCreateDto newDto) {
        final Long newId = idSequence.getAndIncrement();
        final Forklift newForklift = new Forklift(
                newId,
                newDto.layoutId(),
                newDto.operatorId(),
                ForkliftStatus.IN_GARAGE,
                0,
                0
        );
        this.forkliftsMap.put(newId, newForklift);
        return newForklift;
    }

    public Optional<Forklift> getById(final Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(this.forkliftsMap.get(id));
    }

    public List<Forklift> findByLayoutId(final Long layoutId) {
        if (layoutId == null) {
            return Collections.emptyList();
        }
        return this.forkliftsMap.values().stream()
                .filter(forklift -> layoutId.equals(forklift.getLayoutId()))
                .toList();
    }

    public List<Forklift> findFreeByLayoutId(final Long layoutId) {
        if (layoutId == null) {
            return Collections.emptyList();
        }
        final List<Forklift> layoutForklifts = this.findByLayoutId(layoutId);
        return layoutForklifts.stream()
                .filter(Forklift::isFree)
                .toList();
    }

    public boolean areAllForkliftsFreeInGarage(final Long layoutId) {
        if (layoutId == null) {
            return true;
        }
        final List<Forklift> layoutForklifts = findByLayoutId(layoutId);
        return layoutForklifts.stream()
                .allMatch(forklift -> forklift.isFree() && forklift.getX() == 0 && forklift.getY() == 0);
    }

    public void removeByLayoutId(final Long layoutId) {
        if (layoutId == null) {
            return;
        }
        this.forkliftsMap.entrySet().removeIf(entry -> layoutId.equals(entry.getValue().getLayoutId()));
    }

    public Collection<Forklift> getAll() {
        return this.forkliftsMap.values();
    }

    public Optional<Forklift> remove(final Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(this.forkliftsMap.remove(id));
    }
}
