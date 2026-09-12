package io.swiczka.github.apiwarehouse.consumer;

import io.swiczka.github.apiwarehouse.packageitem.PackageItemService;
import io.swiczka.github.sharedcommon.events.PackageStoredEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PackageStoredEventConsumerTest {

    @Mock
    private PackageItemService packageService;

    @InjectMocks
    private PackageStoredEventConsumer consumer;

    @Test
    @DisplayName("Should process PackageStoredEvent and delegate to packageService")
    void handlePackageLocationUpdated_delegatesToService() {
        // given
        final Long packageId = 42L;
        final Long layoutId = 10L;
        final int x = 5;
        final int y = 7;
        final PackageStoredEvent event = new PackageStoredEvent(packageId, layoutId, x, y);

        // when
        consumer.handlePackageLocationUpdated(event);

        // then
        verify(packageService).markPackageAsStored(packageId, x, y);
    }
}
