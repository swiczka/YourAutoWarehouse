package io.swiczka.github.apiwarehouse.packageitem;

import io.swiczka.github.apiwarehouse.dao.LayoutDAO;
import io.swiczka.github.apiwarehouse.dao.PackageItemDAO;
import io.swiczka.github.apiwarehouse.entity.Layout;
import io.swiczka.github.apiwarehouse.entity.PackageItem;
import io.swiczka.github.apiwarehouse.enums.PackageStatus;
import io.swiczka.github.apiwarehouse.producers.PackageEventProducer;
import io.swiczka.github.apiwarehouse.strategy.PackagePlacementStrategy;
import io.swiczka.github.sharedcommon.helpers.Coordinate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.swiczka.github.apiwarehouse.exceptions.ForbiddenException;
import io.swiczka.github.apiwarehouse.exceptions.LayoutNotFoundException;
import io.swiczka.github.apiwarehouse.layout.dto.LayoutOwnerDto;
import io.swiczka.github.apiwarehouse.packageitem.response.PackageItemResponse;
import org.junit.jupiter.api.Nested;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class PackageItemServiceTest {
    @Mock
    private PackageItemDAO packageDAO;
    @Mock
    private LayoutDAO layoutDAO;
    @Mock
    private PackagePlacementStrategy packagePlacementStrategy;
    @Mock
    private PackageEventProducer eventProducer; // <-- brakowało tego mocka!
    @InjectMocks
    private PackageItemService packageItemService;

    private static final UUID TEST_USER_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private static final UUID OTHER_USER_ID = UUID.fromString("660e8400-e29b-41d4-a716-446655440000");

    @Test
    @DisplayName("Should allocate coordinates, save package, and publish event")
    void addNewPackages_savesPackageSuccessfully() {
        // given
        final List<String> packageNames = List.of("Package1");
        final Long inboundOrderId = 10L;
        final Long layoutId = 20L;
        final Coordinate targetCoordinate = new Coordinate(3, 7);
        when(layoutDAO.findById(layoutId)).thenReturn(Optional.of(new Layout()));
        when(packageDAO.findOccupiedCoordinates(layoutId)).thenReturn(new HashSet<>());
        when(packagePlacementStrategy.findTargetLocation(any(), any())).thenReturn(targetCoordinate);
        // when
        packageItemService.addNewPackages(packageNames, inboundOrderId, layoutId);
        // then - weryfikacja zapisu w bazie
        final ArgumentCaptor<PackageItem> captor = ArgumentCaptor.forClass(PackageItem.class);
        verify(packageDAO).save(captor.capture());
        final PackageItem savedItem = captor.getValue();
        assertThat(savedItem.getName()).isEqualTo("Package1");
        assertThat(savedItem.getInboundOrderId()).isEqualTo(inboundOrderId);
        assertThat(savedItem.getLayoutId()).isEqualTo(layoutId);
        assertThat(savedItem.getStatus()).isEqualTo(PackageStatus.ALLOCATED);
        assertThat(savedItem.getX()).isEqualTo(3);
        assertThat(savedItem.getY()).isEqualTo(7);
        // then - weryfikacja wysłania zdarzenia na Kafkę
        verify(eventProducer).sendPackageAllocated(any());
    }

    @Nested
    @DisplayName("getPackagesByInboundOrderId")
    class GetPackagesByInboundOrderId {

        @Test
        @DisplayName("Should return packages when user owns the layout")
        void getPackagesByInboundOrderId_ok() {
            // given
            final Long inboundOrderId = 10L;
            final Long layoutId = 20L;
            final PackageItem packageItem = new PackageItem(
                    layoutId,
                    "Package1",
                    inboundOrderId,
                    null,
                    2,
                    5,
                    PackageStatus.ALLOCATED
            );
            packageItem.setId(100L);

            when(packageDAO.findByInboundOrderId(inboundOrderId)).thenReturn(List.of(packageItem));
            when(layoutDAO.findOwnerById(layoutId)).thenReturn(Optional.of(new LayoutOwnerDto(layoutId, TEST_USER_ID)));

            // when
            final List<PackageItemResponse> result =
                    packageItemService.getPackagesByInboundOrderId(inboundOrderId, TEST_USER_ID);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().id()).isEqualTo(100L);
            assertThat(result.getFirst().name()).isEqualTo("Package1");
            assertThat(result.getFirst().inboundOrderId()).isEqualTo(inboundOrderId);
            assertThat(result.getFirst().layoutId()).isEqualTo(layoutId);
        }

        @Test
        @DisplayName("Should return empty list when no packages found for inbound order")
        void getPackagesByInboundOrderId_empty() {
            // given
            final Long inboundOrderId = 999L;
            when(packageDAO.findByInboundOrderId(inboundOrderId)).thenReturn(List.of());

            // when
            final List<PackageItemResponse> result =
                    packageItemService.getPackagesByInboundOrderId(inboundOrderId, TEST_USER_ID);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should throw ForbiddenException when user is not the layout owner")
        void getPackagesByInboundOrderId_forbidden() {
            // given
            final Long inboundOrderId = 10L;
            final Long layoutId = 20L;
            final PackageItem packageItem = new PackageItem(
                    layoutId,
                    "Package1",
                    inboundOrderId,
                    null,
                    2,
                    5,
                    PackageStatus.ALLOCATED
            );

            when(packageDAO.findByInboundOrderId(inboundOrderId)).thenReturn(List.of(packageItem));
            when(layoutDAO.findOwnerById(layoutId)).thenReturn(Optional.of(new LayoutOwnerDto(layoutId, OTHER_USER_ID)));

            // when / then
            assertThatThrownBy(() -> packageItemService.getPackagesByInboundOrderId(inboundOrderId, TEST_USER_ID))
                    .isInstanceOf(ForbiddenException.class)
                    .hasMessageContaining("Access denied to packages for order with id 10");
        }

        @Test
        @DisplayName("Should throw LayoutNotFoundException when layout does not exist")
        void getPackagesByInboundOrderId_layoutNotFound() {
            // given
            final Long inboundOrderId = 10L;
            final Long layoutId = 20L;
            final PackageItem packageItem = new PackageItem(
                    layoutId,
                    "Package1",
                    inboundOrderId,
                    null,
                    2,
                    5,
                    PackageStatus.ALLOCATED
            );

            when(packageDAO.findByInboundOrderId(inboundOrderId)).thenReturn(List.of(packageItem));
            when(layoutDAO.findOwnerById(layoutId)).thenReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> packageItemService.getPackagesByInboundOrderId(inboundOrderId, TEST_USER_ID))
                    .isInstanceOf(LayoutNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("markPackageAsStored tests")
    class MarkPackageAsStored {

        @Test
        @DisplayName("Should update coordinates, set status to STORED, and save package when found")
        void markPackageAsStored_packageFound() {
            // given
            final Long packageId = 1L;
            final int newX = 3;
            final int newY = 4;
            final PackageItem existingPackage = new PackageItem(
                    10L,
                    "Package1",
                    100L,
                    null,
                    0,
                    0,
                    PackageStatus.ALLOCATED
            );

            when(packageDAO.findById(packageId)).thenReturn(Optional.of(existingPackage));

            // when
            packageItemService.markPackageAsStored(packageId, newX, newY);

            // then
            assertThat(existingPackage.getX()).isEqualTo(newX);
            assertThat(existingPackage.getY()).isEqualTo(newY);
            assertThat(existingPackage.getStatus()).isEqualTo(PackageStatus.STORED);
            verify(packageDAO).save(existingPackage);
        }

        @Test
        @DisplayName("Should not throw and not save package when package not found")
        void markPackageAsStored_packageNotFound() {
            // given
            final Long packageId = 999L;
            when(packageDAO.findById(packageId)).thenReturn(Optional.empty());

            // when
            packageItemService.markPackageAsStored(packageId, 3, 4);

            // then
            verify(packageDAO, never()).save(any());
        }
    }
}
