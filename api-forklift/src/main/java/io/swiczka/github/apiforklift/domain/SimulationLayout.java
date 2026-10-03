package io.swiczka.github.apiforklift.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swiczka.github.sharedcommon.helpers.Coordinate;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SimulationLayout {
    private final Long id;
    private final UUID userId;
    private final Integer forkliftNumber;
    private final List<SimulationGridData> simulationGridData;
    private final Map<Coordinate, SimulationGridData> cellIndex;
    private volatile Instant lastActivityAt;
    private volatile boolean markedToRemove;

    @JsonCreator
    public SimulationLayout(
            @JsonProperty("id") final Long id,
            @JsonProperty("user_id") final UUID userId,
            @JsonProperty("forklift_number") final Integer forkliftNumber,
            @JsonProperty("grid_data") final List<SimulationGridData> simulationGridData
    ) {
        this.id = id;
        this.userId = userId;
        this.forkliftNumber = forkliftNumber;
        this.simulationGridData = simulationGridData != null ? simulationGridData : Collections.emptyList();
        this.cellIndex = this.simulationGridData.stream()
                .filter(cell -> cell.coordinates() != null)
                .collect(Collectors.toUnmodifiableMap(
                        SimulationGridData::coordinates,
                        Function.identity(),
                        (existing, replacement) -> existing
                ));
        this.lastActivityAt = Instant.now();
        this.markedToRemove = false;
    }

    public Long getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public Integer getForkliftNumber() {
        return forkliftNumber;
    }

    public List<SimulationGridData> getGridData() {
        return simulationGridData;
    }

    public Optional<SimulationGridData> getCell(final Coordinate coordinate) {
        if (coordinate == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(cellIndex.get(coordinate));
    }

    public Optional<SimulationGridData> getCell(final int x, final int y) {
        return getCell(new Coordinate(x, y));
    }

    public int getMaxX() {
        return this.simulationGridData.stream()
                .filter(cell -> cell.coordinates() != null)
                .mapToInt(cell -> cell.coordinates().x())
                .max()
                .orElse(0);
    }

    public int getMaxY() {
        return this.simulationGridData.stream()
                .filter(cell -> cell.coordinates() != null)
                .mapToInt(cell -> cell.coordinates().y())
                .max()
                .orElse(0);
    }

    public Instant getLastActivityAt() {
        return lastActivityAt;
    }

    public void touch() {
        this.lastActivityAt = Instant.now();
    }

    public boolean isInactiveFor(final Duration duration) {
        if (duration == null) {
            return false;
        }
        return Duration.between(this.lastActivityAt, Instant.now()).compareTo(duration) >= 0;
    }

    public void markForRemoval() {
        this.markedToRemove = true;
    }

    public boolean isMarkedToRemove() {
        return markedToRemove;
    }
}
