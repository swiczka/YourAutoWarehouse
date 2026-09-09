package io.swiczka.github.apiwarehouse.strategy;

import io.swiczka.github.apiwarehouse.entity.Layout;
import io.swiczka.github.sharedcommon.helpers.Coordinate;

import java.util.Collections;
import java.util.Set;

public interface PackagePlacementStrategy {
    Coordinate findTargetLocation(final Layout layout, final Set<Coordinate> occupiedSpots);

    default Coordinate findTargetLocation(final Layout layout) {
        return findTargetLocation(layout, Collections.emptySet());
    }
}

