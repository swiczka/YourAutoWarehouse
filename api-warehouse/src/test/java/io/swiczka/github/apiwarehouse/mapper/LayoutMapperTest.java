package io.swiczka.github.apiwarehouse.mapper;

import io.swiczka.github.apiwarehouse.domain.GridData;
import io.swiczka.github.apiwarehouse.entity.Layout;
import io.swiczka.github.apiwarehouse.layout.dto.GridDataDto;
import io.swiczka.github.apiwarehouse.layout.response.LayoutReadResponse;
import io.swiczka.github.sharedcommon.helpers.Coordinate;
import io.swiczka.github.sharedcommon.helpers.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


class LayoutMapperTest {

    private Layout testLayout;
    private LayoutReadResponse testDto;

    private List<GridData> gridData;
    private List<GridDataDto> gridDataDto;

    private final static UUID TEST_USER_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private final static Instant TEST_TIME = Instant.parse("2007-12-03T10:15:30.00Z");

    @BeforeEach
    void setUp() {
        gridData = List.of(
                new GridData(
                        Set.of(Direction.DOWN),
                        new Coordinate(1, 0),
                        false,
                        true
                )
        );

        gridDataDto = List.of(
                new GridDataDto(
                        Set.of(Direction.DOWN),
                        new Coordinate(1, 0),
                        false,
                        true
                )
        );

        testLayout = new Layout(
                TEST_USER_UUID,
                TEST_TIME,
                5,
                gridData
                );

        testLayout.setId(1L);

        testDto = new LayoutReadResponse(
            1L,
                TEST_USER_UUID,
                TEST_TIME,
                5,
                gridDataDto
        );
    }

    @Nested
    class ToDto{
        @Test
        @DisplayName("Should return mapped LayoutReadResponse value")
        void toDto_ok(){
            //given - in setUp()

            //when
            LayoutReadResponse response = LayoutMapper.toDto(testLayout);

            //then
            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo(testLayout.getId());
            assertThat(response.userId()).isEqualTo(testLayout.getUserId());
            assertThat(response.forkliftNumber()).isEqualTo(testLayout.getForkliftNumber());
            assertThat(response.createdAt()).isEqualTo(testLayout.getCreatedAt());
            assertThat(response.gridData()).hasSameSizeAs(testLayout.getGridData());

            //then - gridData
            assertThat(response.gridData()
                    .getFirst()
                    .coordinates())
                    .isEqualTo(testLayout.getGridData()
                            .getFirst()
                            .coordinates());
        }

        @Test
        @DisplayName("Should return null when given null")
        void toDto_nullParameter(){
            //given
            Layout testNullLayout = null;

            //when
            LayoutReadResponse response = LayoutMapper.toDto(testNullLayout);

            //then
            assertThat(response).isNull();
        }

        @Test
        @DisplayName("toDto should handle null gridData list")
        void toDto_nullGridData_returnsNullGridData() {
            // given
            Layout layout = new Layout(UUID.randomUUID(), Instant.now(), 3, null);
            layout.setId(1L);

            // when
            LayoutReadResponse response = LayoutMapper.toDto(layout);

            // then
            assertThat(response.gridData()).isNull();
        }
    }

    @Nested
    class ToEntity {
        @Test
        @DisplayName("Should return mapped Layout value")
        void toEntity_ok(){
            //given - in setUp()

            //when
            Layout response = LayoutMapper.toEntity(testDto);

            //then
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(testDto.id());
            assertThat(response.getUserId()).isEqualTo(testDto.userId());
            assertThat(response.getForkliftNumber()).isEqualTo(testDto.forkliftNumber());
            assertThat(response.getCreatedAt()).isEqualTo(testDto.createdAt());
            assertThat(response.getGridData()).hasSameSizeAs(testDto.gridData());

            //then - gridData
            assertThat(response.getGridData()
                    .getFirst()
                    .coordinates())
                    .isEqualTo(testDto.gridData()
                            .getFirst()
                            .coordinates());
        }

        @Test
        @DisplayName("Should return null when given null")
        void toEntity_nullParameter(){
            //given
            LayoutReadResponse testNullLayoutDto = null;

            //when
            Layout response = LayoutMapper.toEntity(testNullLayoutDto);

            //then
            assertThat(response).isNull();
        }

        @Test
        @DisplayName("toEntity should handle null gridData list")
        void toEntity_nullGridData_returnsNullGridData() {
            // given
            LayoutReadResponse layoutDto = new LayoutReadResponse(1L, UUID.randomUUID(), Instant.now(), 3, null);

            // when
            Layout response = LayoutMapper.toEntity(layoutDto);

            // then
            assertThat(response.getGridData()).isNull();
        }
    }
}