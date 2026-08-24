package io.swiczka.github.apiwarehouse.layout;

import io.swiczka.github.apiwarehouse.dao.LayoutDAO;
import io.swiczka.github.apiwarehouse.domain.GridData;
import io.swiczka.github.apiwarehouse.entity.Layout;
import io.swiczka.github.apiwarehouse.exceptions.LayoutNotFoundException;
import io.swiczka.github.apiwarehouse.layout.dto.GridDataDto;
import io.swiczka.github.apiwarehouse.layout.helpers.Direction;
import io.swiczka.github.apiwarehouse.layout.response.LayoutReadResponse;
import io.swiczka.github.sharedcommon.helpers.Coordinate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WarehouseLayoutServiceTest {

    @Mock
    private LayoutDAO layoutDAO;

    @InjectMocks
    private WarehouseLayoutService layoutService;

    private static final UUID TEST_USER_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private static final Long TEST_LAYOUT_ID = 1L;

    private Layout testLayout;
    private List<GridDataDto> testGridDataDtos;

    @BeforeEach
    void setUp() {
        GridData gridData = new GridData(
                Set.of(Direction.UP, Direction.RIGHT),
                new Coordinate(0, 1),
                false,
                true
        );

        testLayout = new Layout(TEST_USER_ID, Instant.now(), 5, List.of(gridData));
        testLayout.setId(TEST_LAYOUT_ID);

        testGridDataDtos = List.of(
                new GridDataDto(
                        Set.of(Direction.UP, Direction.RIGHT),
                        new Coordinate(0, 1),
                        false,
                        true
                )
        );
    }

    @Nested
    @DisplayName("Save layout using DAO")
    class SaveNewLayout {

        @Test
        @DisplayName("Should map dtos, save layout and return response")
        void saveNewLayout_ok() {
            //given
            ArgumentCaptor<Layout> layoutCaptor = ArgumentCaptor.forClass(Layout.class);

            //when
            LayoutReadResponse response = layoutService.saveNewLayout(testGridDataDtos, TEST_USER_ID);

            //then - what went to DAO
            verify(layoutDAO).save(layoutCaptor.capture());
            Layout savedEntity = layoutCaptor.getValue();

            assertThat(savedEntity.getUserId()).isEqualTo(TEST_USER_ID);
            assertThat(savedEntity.getGridData()).hasSize(1);
            assertThat(savedEntity.getForkliftNumber()).isEqualTo(5);

            //then - what is returned
            assertThat(response).isNotNull();
            assertThat(response.forkliftNumber()).isEqualTo(5);
            assertThat(response.gridData()).hasSize(1);
            assertThat(response.userId()).isEqualTo(TEST_USER_ID);
        }
    }

    @Nested
    @DisplayName("getUserLayout")
    class getUserLayout {

        @Test
        @DisplayName("Should return user's layout when found")
        void getUserLayout_ok(){
            //given
            when(layoutDAO.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(testLayout));

            //when
            LayoutReadResponse response = layoutService.getUserLayout(TEST_USER_ID);

            //then
            assertThat(response).isNotNull();
            assertThat(response.userId()).isEqualTo(TEST_USER_ID);
            assertThat(response.id()).isEqualTo(TEST_LAYOUT_ID);
        }

        @Test
        @DisplayName("Should throw LayoutNotFoundException when not found")
        void getUserLayout_layoutNotFound(){
            //given
            when(layoutDAO.findByUserId(TEST_USER_ID)).thenReturn(Optional.empty());

            //when then
            assertThatThrownBy(() -> layoutService.getUserLayout(TEST_USER_ID))
                    .isInstanceOf(LayoutNotFoundException.class)
                    .hasMessageContaining(TEST_USER_ID.toString());
        }

    }

    @Nested
    @DisplayName("updateForkliftNumber")
    class updateForkliftNumber {

        private final int newForkliftCount = 3;

        @Test
        @DisplayName("Should update forklift number and return response")
        void updateForkliftNumber_ok(){
            //given
            when(layoutDAO.findById(TEST_LAYOUT_ID)).thenReturn(Optional.of(testLayout));
            ArgumentCaptor<Layout> captor = ArgumentCaptor.forClass(Layout.class);

            //when
            LayoutReadResponse response = layoutService.updateForkliftNumber(TEST_LAYOUT_ID, newForkliftCount);

            //then
            verify(layoutDAO).findById(TEST_LAYOUT_ID);
            verify(layoutDAO).save(captor.capture());
            Layout savedEntity = captor.getValue();

            //then - verifying that we sent updated entity to DAO
            assertThat(savedEntity.getForkliftNumber()).isEqualTo(newForkliftCount);

            //then - verifying that returned entity is correct
            assertThat(response.forkliftNumber()).isEqualTo(newForkliftCount);
            assertThat(response.id()).isEqualTo(TEST_LAYOUT_ID);
            assertThat(response.gridData()).hasSize(1);
        }

        @Test
        @DisplayName("Should throw LayoutNotFoundException when layout not found")
        void updateForkliftNumber_notFound(){
            //given
            when(layoutDAO.findById(TEST_LAYOUT_ID)).thenReturn(Optional.empty());

            //when then
            assertThatThrownBy(() -> layoutService.updateForkliftNumber(TEST_LAYOUT_ID, newForkliftCount))
                    .isInstanceOf(LayoutNotFoundException.class)
                    .hasMessageContaining(TEST_LAYOUT_ID.toString());

        }
    }
}