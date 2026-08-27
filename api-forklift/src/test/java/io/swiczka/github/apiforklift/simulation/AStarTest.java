package io.swiczka.github.apiforklift.simulation;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swiczka.github.apiforklift.domain.SimulationLayout;
import io.swiczka.github.sharedcommon.helpers.Coordinate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AStarTest {

    private static SimulationLayout sampleLayout;

    //beforeAll because why bother loading big json for every test individually
    @BeforeAll
    static void setUpAll() throws Exception {
        final ObjectMapper objectMapper = new ObjectMapper();
        final ClassPathResource resource = new ClassPathResource("data/sample_layout.json");
        try (final InputStream inputStream = resource.getInputStream()) {
            sampleLayout = objectMapper.readValue(inputStream, SimulationLayout.class);
        }
    }

    @Test
    @DisplayName("should find valid path following allowed directions on sample layout")
    void shouldFindPathOnSampleLayout() {
        // given
        final Coordinate start = new Coordinate(0, 0);
        final Coordinate target = new Coordinate(2, 0);

        // when
        final List<Coordinate> path = AStar.findPath(start, target, sampleLayout);

        // then
        assertThat(path).isNotEmpty();
        assertThat(path.getFirst()).isEqualTo(start);
        assertThat(path.getLast()).isEqualTo(target);
    }

    @Test
    @DisplayName("should return single element when start equals target")
    void shouldReturnSingleElementWhenStartEqualsTarget() {
        // given
        final Coordinate start = new Coordinate(0, 0);

        // when
        final List<Coordinate> path = AStar.findPath(start, start, sampleLayout);

        // then
        assertThat(path).containsExactly(start);
    }

    @Test
    @DisplayName("should return empty list when target is not a road")
    void shouldReturnEmptyListWhenTargetNotRoad() {
        // given - coordinate that is not road
        final Coordinate start = new Coordinate(0, 0);
        final Coordinate nonRoadTarget = new Coordinate(99, 99);

        // when
        final List<Coordinate> path = AStar.findPath(start, nonRoadTarget, sampleLayout);

        // then
        assertThat(path).isEmpty();
    }
}
