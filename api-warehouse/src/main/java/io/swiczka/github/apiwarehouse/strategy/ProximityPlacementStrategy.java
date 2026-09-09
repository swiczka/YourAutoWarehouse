package io.swiczka.github.apiwarehouse.strategy;

import io.swiczka.github.apiwarehouse.domain.GridData;
import io.swiczka.github.apiwarehouse.entity.Layout;
import io.swiczka.github.apiwarehouse.exceptions.WarehouseFullException;
import io.swiczka.github.sharedcommon.helpers.Coordinate;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Set;

@Component
public class ProximityPlacementStrategy implements PackagePlacementStrategy {

    @Override
    public Coordinate findTargetLocation(final Layout layout, final Set<Coordinate> occupiedSpots) {
        final Coordinate deliveryZone = new Coordinate(
                0,
                layout.getMaxY()
        );

        return layout.getGridData()
                .stream()
                .filter(cell -> Boolean.TRUE.equals(cell.isShelf()))
                .filter(cell -> occupiedSpots == null || !occupiedSpots.contains(cell.coordinates()))
                .min(Comparator.comparingInt(cell -> manhattanDistance(cell.coordinates(), deliveryZone)))
                .map(GridData::coordinates)
                .orElseThrow(() -> new WarehouseFullException(
                        String.format("Unable to find free spot in warehouse (layout id: %d)", layout.getId())
                ));
    }

    private int manhattanDistance(final Coordinate a, final Coordinate b) {
        return Math.abs(a.x() - b.x()) + Math.abs(a.y() - b.y());
    }
}
