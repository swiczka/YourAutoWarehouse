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
