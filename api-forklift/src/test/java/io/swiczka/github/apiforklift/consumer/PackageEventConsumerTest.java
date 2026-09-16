package io.swiczka.github.apiforklift.consumer;

import io.swiczka.github.apiforklift.dto.ForkliftTaskCreateDto;
import io.swiczka.github.sharedcommon.helpers.TaskType;
import io.swiczka.github.apiforklift.simulation.ForkliftTaskRegistry;
import io.swiczka.github.apiforklift.simulation.LayoutCache;
import io.swiczka.github.sharedcommon.events.PackageAllocatedEvent;
import io.swiczka.github.sharedcommon.events.PackageShouldBeSentEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PackageEventConsumerTest {

    @Mock
    private ForkliftTaskRegistry taskRegistry;

    @Mock
    private LayoutCache layoutCache;

    @InjectMocks
    private PackageEventConsumer packageEventConsumer;

    @Test
    @DisplayName("Should create forklift task with current and target coordinates from event")
    void handlePackageAllocated_createsTaskWithCoordinatesFromEvent() {
        // given
        final Long layoutId = 5L;
        final Long packageId = 42L;
        final Long inboundOrderId = 10L;
        final int currentX = 0;
        final int currentY = 12;
        final int targetX = 3;
        final int targetY = 7;

        final PackageAllocatedEvent event = new PackageAllocatedEvent(
                packageId,
                layoutId,
                inboundOrderId,
                "Box-1",
                "ALLOCATED",
                currentX,
                currentY,
                targetX,
                targetY
        );

        // when
        packageEventConsumer.handlePackageAllocated(event);

        // then
        final ArgumentCaptor<ForkliftTaskCreateDto> captor = ArgumentCaptor.forClass(ForkliftTaskCreateDto.class);
        verify(taskRegistry).add(captor.capture());

        final ForkliftTaskCreateDto createdDto = captor.getValue();
        assertThat(createdDto.packageItemId()).isEqualTo(packageId);
        assertThat(createdDto.layoutId()).isEqualTo(layoutId);
        assertThat(createdDto.sourceX()).isEqualTo(currentX);
        assertThat(createdDto.sourceY()).isEqualTo(currentY);
        assertThat(createdDto.targetX()).isEqualTo(targetX);
        assertThat(createdDto.targetY()).isEqualTo(targetY);
        assertThat(createdDto.taskType()).isEqualTo(TaskType.INBOUND);
    }

    @Test
    @DisplayName("Should create outbound forklift task when package should be sent")
    void handlePackageShouldBeSent_createsOutboundTaskWithCoordinatesFromEvent() {
        // given
        final Long layoutId = 5L;
        final Long packageId = 42L;
        final Long outboundOrderId = 20L;
        final int currentX = 3;
        final int currentY = 7;
        final int targetX = 15;
        final int targetY = 12;

        final PackageShouldBeSentEvent event = new PackageShouldBeSentEvent(
                packageId,
                layoutId,
                outboundOrderId,
                currentX,
                currentY,
                targetX,
                targetY
        );

        // when
        packageEventConsumer.handlePackageShouldBeSent(event);

        // then
        final ArgumentCaptor<ForkliftTaskCreateDto> captor = ArgumentCaptor.forClass(ForkliftTaskCreateDto.class);
        verify(taskRegistry).add(captor.capture());

        final ForkliftTaskCreateDto createdDto = captor.getValue();
        assertThat(createdDto.packageItemId()).isEqualTo(packageId);
        assertThat(createdDto.layoutId()).isEqualTo(layoutId);
        assertThat(createdDto.sourceX()).isEqualTo(currentX);
        assertThat(createdDto.sourceY()).isEqualTo(currentY);
        assertThat(createdDto.targetX()).isEqualTo(targetX);
        assertThat(createdDto.targetY()).isEqualTo(targetY);
        assertThat(createdDto.taskType()).isEqualTo(TaskType.OUTBOUND);
    }
}
