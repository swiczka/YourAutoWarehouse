package io.swiczka.github.apiwarehouse.strategy;

import io.swiczka.github.apiwarehouse.domain.GridData;
import io.swiczka.github.apiwarehouse.entity.Layout;
import io.swiczka.github.apiwarehouse.exceptions.WarehouseFullException;
import io.swiczka.github.sharedcommon.helpers.Coordinate;
import io.swiczka.github.sharedcommon.helpers.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProximityPlacementStrategyTest {

    private ProximityPlacementStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new ProximityPlacementStrategy();
    }

    @Test
    @DisplayName("Should pick the shelf closest to delivery zone (0, maxY)")
    void findTargetLocation_closestToDeliveryZone() {
        // given
        // Shelf A at (0, 8), Shelf B at (0, 2), Road at (0, 10)
        final GridData shelfFar = new GridData(Set.of(), new Coordinate(0, 2), true, false);
        final GridData shelfNear = new GridData(Set.of(), new Coordinate(0, 8), true, false);
        final GridData road = new GridData(Set.of(Direction.UP), new Coordinate(0, 10), false, true);

        final Layout layout = new Layout(UUID.randomUUID(), Instant.now(), 5, List.of(shelfFar, shelfNear, road));

        // when
        final Coordinate target = strategy.findTargetLocation(layout, Set.of());

        // then - maxY is 10, so (0, 8) is distance 2 from (0, 10), while (0, 2) is distance 8
        assertThat(target).isEqualTo(new Coordinate(0, 8));
    }

    @Test
    @DisplayName("Should skip occupied shelf and pick next closest free shelf")
    void findTargetLocation_skipOccupiedShelf() {
        // given
        final GridData shelfFar = new GridData(Set.of(), new Coordinate(0, 2), true, false);
        final GridData shelfNear = new GridData(Set.of(), new Coordinate(0, 8), true, false);
        final GridData road = new GridData(Set.of(Direction.UP), new Coordinate(0, 10), false, true);

        final Layout layout = new Layout(UUID.randomUUID(), Instant.now(), 5, List.of(shelfFar, shelfNear, road));
        final Set<Coordinate> occupiedSpots = Set.of(new Coordinate(0, 8));

        // when
        final Coordinate target = strategy.findTargetLocation(layout, occupiedSpots);

        // then - shelfNear (0, 8) is occupied, so should pick shelfFar (0, 2)
        assertThat(target).isEqualTo(new Coordinate(0, 2));
    }

    @Test
    @DisplayName("Should throw WarehouseFullException when all shelves are occupied")
    void findTargetLocation_warehouseFull() {
        // given
        final GridData shelf = new GridData(Set.of(), new Coordinate(0, 2), true, false);
        final Layout layout = new Layout(UUID.randomUUID(), Instant.now(), 5, List.of(shelf));
        layout.setId(42L);
        final Set<Coordinate> occupiedSpots = Set.of(new Coordinate(0, 2));

        // when / then
        assertThatThrownBy(() -> strategy.findTargetLocation(layout, occupiedSpots))
                .isInstanceOf(WarehouseFullException.class)
                .hasMessageContaining("layout id: 42");
    }
}
