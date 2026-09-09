package io.swiczka.github.apiforklift.consumer;

import io.swiczka.github.apiforklift.domain.SimulationGridData;
import io.swiczka.github.apiforklift.domain.SimulationLayout;
import io.swiczka.github.apiforklift.dto.ForkliftTaskCreateDto;
import io.swiczka.github.apiforklift.simulation.ForkliftTaskRegistry;
import io.swiczka.github.apiforklift.simulation.LayoutCache;
import io.swiczka.github.sharedcommon.events.PackageAllocatedEvent;
import io.swiczka.github.sharedcommon.helpers.Coordinate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PackageEventConsumerTest {

    @Mock
    private ForkliftTaskRegistry taskRegistry;

    @Mock
    private LayoutCache layoutCache;

    @InjectMocks
    private PackageEventConsumer packageEventConsumer;

    @Test
    @DisplayName("Should create forklift task with delivery zone source coordinates from layout")
    void handlePackageAllocated_createsTaskWithDeliveryZone() {
        // given
        final Long layoutId = 5L;
        final Long packageId = 42L;
        final int targetX = 3;
        final int targetY = 7;
        final int maxY = 12;

        final SimulationGridData cell0 = new SimulationGridData(new Coordinate(0, 0), Set.of(), true, false);
        final SimulationGridData cellMax = new SimulationGridData(new Coordinate(0, maxY), Set.of(), true, false);
        final SimulationLayout layout = new SimulationLayout(layoutId, UUID.randomUUID(), 2, List.of(cell0, cellMax));

        when(layoutCache.get(layoutId)).thenReturn(Optional.of(layout));

        final PackageAllocatedEvent event = new PackageAllocatedEvent(packageId, layoutId, "Box-1", targetX, targetY);

        // when
        packageEventConsumer.handlePackageAllocated(event);

        // then
        final ArgumentCaptor<ForkliftTaskCreateDto> captor = ArgumentCaptor.forClass(ForkliftTaskCreateDto.class);
        verify(taskRegistry).add(captor.capture());

        final ForkliftTaskCreateDto createdDto = captor.getValue();
        assertThat(createdDto.packageItemId()).isEqualTo(packageId);
        assertThat(createdDto.layoutId()).isEqualTo(layoutId);
        assertThat(createdDto.sourceX()).isEqualTo(0);
        assertThat(createdDto.sourceY()).isEqualTo(maxY);
        assertThat(createdDto.targetX()).isEqualTo(targetX);
        assertThat(createdDto.targetY()).isEqualTo(targetY);
    }

    @Test
    @DisplayName("Should fallback to sourceY=0 when layout is not in cache")
    void handlePackageAllocated_fallbackWhenLayoutMissing() {
        // given
        final Long layoutId = 99L;
        final Long packageId = 100L;
        final int targetX = 5;
        final int targetY = 5;

        when(layoutCache.get(layoutId)).thenReturn(Optional.empty());

        final PackageAllocatedEvent event = new PackageAllocatedEvent(packageId, layoutId, "Box-2", targetX, targetY);

        // when
        packageEventConsumer.handlePackageAllocated(event);

        // then
        final ArgumentCaptor<ForkliftTaskCreateDto> captor = ArgumentCaptor.forClass(ForkliftTaskCreateDto.class);
        verify(taskRegistry).add(captor.capture());

        final ForkliftTaskCreateDto createdDto = captor.getValue();
        assertThat(createdDto.packageItemId()).isEqualTo(packageId);
        assertThat(createdDto.layoutId()).isEqualTo(layoutId);
        assertThat(createdDto.sourceX()).isEqualTo(0);
        assertThat(createdDto.sourceY()).isEqualTo(0);
        assertThat(createdDto.targetX()).isEqualTo(targetX);
        assertThat(createdDto.targetY()).isEqualTo(targetY);
    }
}
