package io.swiczka.github.apiwarehouse.consumer;

import io.swiczka.github.apiwarehouse.packageitem.PackageItemService;
import io.swiczka.github.sharedcommon.events.InboundOrderCreatedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderEventConsumerTest {

    @Mock
    private PackageItemService packageService;

    @InjectMocks
    private OrderEventConsumer orderEventConsumer;

    @Test
    @DisplayName("Should process InboundOrderCreatedEvent and call addNewPackage for each package")
    void handleInboundOrderCreated_processesPackages() {
        // given
        final Long orderId = 123L;
        final UUID operatorId = UUID.randomUUID();
        final Long companyId = 99L;
        final Long layoutId = 456L;
        final List<String> packages = List.of("Pack-1", "Pack-2");
        final InboundOrderCreatedEvent event = new InboundOrderCreatedEvent(
                orderId,
                operatorId,
                companyId,
                layoutId,
                packages
        );

        // when
        orderEventConsumer.handleInboundOrderCreated(event);

        // then
        verify(packageService).addNewPackages(packages, orderId, layoutId);
    }
}
