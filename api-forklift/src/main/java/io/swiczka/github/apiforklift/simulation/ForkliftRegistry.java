package io.swiczka.github.apiforklift.simulation;

import io.swiczka.github.apiforklift.domain.Forklift;
import io.swiczka.github.apiforklift.dto.ForkliftCreateDto;
import io.swiczka.github.apiforklift.enums.ForkliftStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class ForkliftRegistry {
    private final ConcurrentHashMap<Long, Forklift> forkliftsMap;

    private final AtomicLong idSequence;

    public ForkliftRegistry() {
        this.forkliftsMap = new ConcurrentHashMap<Long, Forklift>();
        this.idSequence = new AtomicLong();
    }

    public Forklift add(final ForkliftCreateDto newDto){
        Long newId = idSequence.getAndIncrement();
        Forklift newForklift = new Forklift(
                newId,
                newDto.layoutId(),
                newDto.operatorId(),
                ForkliftStatus.IN_GARAGE,
                0,0
        );
        this.forkliftsMap.put(
                newId,
                newForklift
        );
        return newForklift;
    }

    public Optional<Forklift> getById(final Long id){
        Forklift found = this.forkliftsMap.get(id);
        return Optional.of(found);
    }
}
