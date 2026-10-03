package io.swiczka.github.apiforklift.mapper;

import io.swiczka.github.apiforklift.domain.SimulationGridData;
import io.swiczka.github.apiforklift.domain.SimulationLayout;
import io.swiczka.github.sharedcommon.dto.WarehouseLayoutDto;
import io.swiczka.github.sharedcommon.events.GridCellEvent;
import io.swiczka.github.sharedcommon.events.LayoutLoadedEvent;
import io.swiczka.github.sharedcommon.events.LayoutSavedEvent;

import java.util.Collections;
import java.util.List;

public final class SimulationLayoutMapper {

    private SimulationLayoutMapper() {
        // Utility class
    }

    public static SimulationLayout toSimulation(final LayoutSavedEvent event) {
        if (event == null) {
            return null;
        }

        final List<SimulationGridData> gridData = event.gridData() != null
                ? event.gridData().stream()
                        .map(SimulationLayoutMapper::toSimulationGridData)
                        .toList()
                : Collections.emptyList();

        return new SimulationLayout(
                event.layoutId(),
                event.userId(),
                event.forkliftNumber(),
                gridData
        );
    }

    public static SimulationLayout toSimulation(final LayoutLoadedEvent event) {
        if (event == null) {
            return null;
        }

        final List<SimulationGridData> gridData = event.gridData() != null
                ? event.gridData().stream()
                        .map(SimulationLayoutMapper::toSimulationGridData)
                        .toList()
                : Collections.emptyList();

        return new SimulationLayout(
                event.layoutId(),
                event.userId(),
                event.forkliftNumber(),
                gridData
        );
    }

    public static SimulationLayout toSimulation(final WarehouseLayoutDto dto) {
        if (dto == null) {
            return null;
        }

        final List<SimulationGridData> gridData = dto.gridData() != null
                ? dto.gridData().stream()
                        .map(SimulationLayoutMapper::toSimulationGridData)
                        .toList()
                : Collections.emptyList();

        return new SimulationLayout(
                dto.id(),
                dto.userId(),
                dto.forkliftNumber(),
                gridData
        );
    }

    private static SimulationGridData toSimulationGridData(final GridCellEvent cellEvent) {
        if (cellEvent == null) {
            return null;
        }

        return new SimulationGridData(
                cellEvent.coordinates(),
                cellEvent.allowedDirections(),
                cellEvent.isRoad(),
                cellEvent.isShelf()
        );
    }
}
