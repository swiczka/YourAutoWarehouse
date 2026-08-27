package io.swiczka.github.apiforklift.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swiczka.github.sharedcommon.helpers.Coordinate;
import io.swiczka.github.sharedcommon.helpers.Direction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class LayoutDeserializationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("should deserialize sample_layout.json correctly into Layout domain model")
    void shouldDeserializeSampleLayout() throws Exception {
        // given
        final ClassPathResource resource =
                new ClassPathResource("data/sample_layout.json");
        assertThat(resource.exists()).isTrue();

        // when
        final SimulationLayout layout;
        try (final InputStream inputStream = resource.getInputStream()) {
            layout = objectMapper.readValue(inputStream, SimulationLayout.class);
        }

        // then
        assertThat(layout).isNotNull();
        assertThat(layout.getId()).isEqualTo(24L);
        assertThat(layout.getUserId()).isEqualTo(UUID.fromString("c1aada91-fdab-4c94-b6d5-f888f50ea8dc"));
        assertThat(layout.getForkliftNumber()).isEqualTo(5);
        assertThat(layout.getGridData()).isNotEmpty();

        // check specific cell (0, 1) -> isRoad=true, allowedDirections=[DOWN]
        final Optional<SimulationGridData> cell01 = layout.getCell(0, 1);
        assertThat(cell01).isPresent();
        assertThat(cell01.get().isRoad()).isTrue();
        assertThat(cell01.get().isShelf()).isFalse();
        assertThat(cell01.get().allowedDirections()).containsExactly(Direction.DOWN);

        // check specific cell (0, 0) -> isRoad=true, allowedDirections=[RIGHT]
        final Optional<SimulationGridData> cell00 = layout.getCell(new Coordinate(0, 0));
        assertThat(cell00).isPresent();
        assertThat(cell00.get().allowedDirections()).containsExactly(Direction.RIGHT);
    }
}
