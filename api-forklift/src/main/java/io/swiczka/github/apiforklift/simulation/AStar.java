package io.swiczka.github.apiforklift.simulation;

import io.swiczka.github.apiforklift.domain.SimulationGridData;
import io.swiczka.github.apiforklift.domain.SimulationLayout;
import io.swiczka.github.sharedcommon.helpers.Coordinate;
import io.swiczka.github.sharedcommon.helpers.Direction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;

public final class AStar {

    private record Node(Coordinate coordinate, int fScore) {}

    private AStar() {
        // Utility class
    }

    public static List<Coordinate> findPath(
            final Coordinate start,
            final Coordinate target,
            final SimulationLayout layout
    ) {
        if (start == null || target == null || layout == null) {
            return Collections.emptyList();
        }

        if (start.equals(target)) {
            return List.of(start);
        }

        final Optional<SimulationGridData> startCellOpt = layout.getCell(start);
        final Optional<SimulationGridData> targetCellOpt = layout.getCell(target);

        if (startCellOpt.isEmpty() || targetCellOpt.isEmpty()) {
            return Collections.emptyList();
        }

        final boolean isStartPassable = Boolean.TRUE.equals(startCellOpt.get().isRoad());
        final boolean isTargetPassable = Boolean.TRUE.equals(targetCellOpt.get().isRoad());

        if (!isStartPassable || !isTargetPassable) {
            return Collections.emptyList();
        }

        final PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingInt(Node::fScore));
        final Map<Coordinate, Coordinate> cameFrom = new HashMap<>();
        final Map<Coordinate, Integer> gScore = new HashMap<>();

        gScore.put(start, 0);
        openSet.add(new Node(start, heuristic(start, target)));

        while (!openSet.isEmpty()) {
            final Node current = openSet.poll();
            final Coordinate currentCoord = current.coordinate();

            if (currentCoord.equals(target)) {
                return reconstructPath(cameFrom, currentCoord);
            }

            final Optional<SimulationGridData> currentCellOpt = layout.getCell(currentCoord);
            if (currentCellOpt.isEmpty()) {
                continue;
            }

            final List<SimulationGridData> neighbors = getNeighbors(currentCellOpt.get(), layout);

            for (final SimulationGridData neighbor : neighbors) {
                final Coordinate neighborCoord = neighbor.coordinates();
                final int tentativeGScore = gScore.get(currentCoord) + 1;
                final int currentNeighborGScore = gScore.getOrDefault(neighborCoord, Integer.MAX_VALUE);

                if (tentativeGScore < currentNeighborGScore) {
                    cameFrom.put(neighborCoord, currentCoord);
                    gScore.put(neighborCoord, tentativeGScore);
                    final int fScore = tentativeGScore + heuristic(neighborCoord, target);
                    openSet.add(new Node(neighborCoord, fScore));
                }
            }
        }

        return Collections.emptyList();
    }

    public static List<SimulationGridData> getNeighbors(
            final SimulationGridData currentCell,
            final SimulationLayout layout
    ) {
        if (currentCell == null || currentCell.allowedDirections() == null || layout == null) {
            return Collections.emptyList();
        }

        final List<SimulationGridData> neighbors = new ArrayList<>();

        for (final Direction direction : currentCell.allowedDirections()) {
            int nextX = currentCell.coordinates().x();
            int nextY = currentCell.coordinates().y();

            switch (direction) {
                case UP -> nextY = currentCell.coordinates().y() + 1;
                case DOWN -> nextY = currentCell.coordinates().y() - 1;
                case LEFT -> nextX = currentCell.coordinates().x() - 1;
                case RIGHT -> nextX = currentCell.coordinates().x() + 1;
            }

            final SimulationGridData targetCell = layout.getCell(nextX, nextY).orElse(null);

            if (targetCell != null && Boolean.TRUE.equals(targetCell.isRoad())) {
                neighbors.add(targetCell);
            }
        }

        return neighbors;
    }

    private static int heuristic(final Coordinate a, final Coordinate b) {
        return Math.abs(a.x() - b.x()) + Math.abs(a.y() - b.y());
    }

    private static List<Coordinate> reconstructPath(
            final Map<Coordinate, Coordinate> cameFrom,
            Coordinate current
    ) {
        final List<Coordinate> totalPath = new ArrayList<>();
        totalPath.add(current);

        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            totalPath.add(0, current);
        }

        return totalPath;
    }
}
