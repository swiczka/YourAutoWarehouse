package io.swiczka.github.apiwarehouse.mapper;

import io.swiczka.github.apiwarehouse.domain.GridData;
import io.swiczka.github.apiwarehouse.layout.dto.GridDataDto;
import io.swiczka.github.sharedcommon.helpers.Coordinate;
import io.swiczka.github.sharedcommon.helpers.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class GridDataMapperTest {

    private GridData testDomain;
    private GridDataDto testDto;

    private static final Set<Direction> TEST_DIRECTIONS = Set.of(Direction.UP, Direction.RIGHT);
    private static final Coordinate TEST_COORDINATE = new Coordinate(2, 3);
    private static final Boolean TEST_IS_SHELF = false;
    private static final Boolean TEST_IS_ROAD = true;

    @BeforeEach
    void setUp() {
        testDomain = new GridData(
                TEST_DIRECTIONS,
                TEST_COORDINATE,
                TEST_IS_SHELF,
                TEST_IS_ROAD
        );

        testDto = new GridDataDto(
                TEST_DIRECTIONS,
                TEST_COORDINATE,
                TEST_IS_SHELF,
                TEST_IS_ROAD
        );
    }

    @Nested
    @DisplayName("toDto")
    class ToDto {

        @Test
        @DisplayName("Should return mapped GridDataDto value from GridData domain")
        void toDto_ok() {
            // given - in setUp()

            // when
            final GridDataDto response = GridDataMapper.toDto(testDomain);

            // then
            assertThat(response).isNotNull();
            assertThat(response.allowedDirections()).isEqualTo(testDomain.allowedDirections());
            assertThat(response.coordinates()).isEqualTo(testDomain.coordinates());
            assertThat(response.isShelf()).isEqualTo(testDomain.isShelf());
            assertThat(response.isRoad()).isEqualTo(testDomain.isRoad());
        }

        @Test
        @DisplayName("Should return null when given null domain")
        void toDto_nullParameter() {
            // given
            final GridData nullDomain = null;

            // when
            final GridDataDto response = GridDataMapper.toDto(nullDomain);

            // then
            assertThat(response).isNull();
        }
    }

    @Nested
    @DisplayName("toDomain")
    class ToDomain {

        @Test
        @DisplayName("Should return mapped GridData domain value from GridDataDto")
        void toDomain_ok() {
            // given - in setUp()

            // when
            final GridData response = GridDataMapper.toDomain(testDto);

            // then
            assertThat(response).isNotNull();
            assertThat(response.allowedDirections()).isEqualTo(testDto.allowedDirections());
            assertThat(response.coordinates()).isEqualTo(testDto.coordinates());
            assertThat(response.isShelf()).isEqualTo(testDto.isShelf());
            assertThat(response.isRoad()).isEqualTo(testDto.isRoad());
        }

        @Test
        @DisplayName("Should return null when given null dto")
        void toDomain_nullParameter() {
            // given
            final GridDataDto nullDto = null;

            // when
            final GridData response = GridDataMapper.toDomain(nullDto);

            // then
            assertThat(response).isNull();
        }
    }
}