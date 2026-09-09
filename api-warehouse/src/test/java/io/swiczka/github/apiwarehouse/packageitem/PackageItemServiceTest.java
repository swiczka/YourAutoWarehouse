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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
}
