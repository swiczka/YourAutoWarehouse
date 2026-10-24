package io.swiczka.github.apiforklift.simulation;

import io.swiczka.github.apiforklift.domain.Forklift;
import io.swiczka.github.apiforklift.dto.ForkliftCreateDto;
import io.swiczka.github.apiforklift.enums.ForkliftStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ForkliftRegistryTest {

    private static final Long LAYOUT_ID = 1L;
    private static final Long OTHER_LAYOUT_ID = 2L;

    private ForkliftRegistry forkliftRegistry;

    @BeforeEach
    void setUp() {
        forkliftRegistry = new ForkliftRegistry();
    }

    private Forklift addForklift(final Long layoutId) {
        return forkliftRegistry.add(new ForkliftCreateDto(layoutId, UUID.randomUUID()));
    }

    @Test
    @DisplayName("should remove newest idle forklifts first")
    void removeIdleInGarage_removesNewestFirst() {
        // given
        final Forklift first = addForklift(LAYOUT_ID);
        final Forklift second = addForklift(LAYOUT_ID);
        final Forklift third = addForklift(LAYOUT_ID);

        // when
        final List<Forklift> removed = forkliftRegistry.removeIdleInGarage(LAYOUT_ID, 2);

        // then
        assertThat(removed).extracting(Forklift::getId).containsExactly(third.getId(), second.getId());
        assertThat(forkliftRegistry.findByLayoutId(LAYOUT_ID)).extracting(Forklift::getId).containsExactly(first.getId());
    }

    @Test
    @DisplayName("should not remove busy or returning forklifts")
    void removeIdleInGarage_skipsBusyForklifts() {
        // given
        final Forklift busy = addForklift(LAYOUT_ID);
        busy.setStatus(ForkliftStatus.CARRYING);
        final Forklift returning = addForklift(LAYOUT_ID);
        returning.setStatus(ForkliftStatus.RETURNING);

        // when
        final List<Forklift> removed = forkliftRegistry.removeIdleInGarage(LAYOUT_ID, 2);

        // then
        assertThat(removed).isEmpty();
        assertThat(forkliftRegistry.findByLayoutId(LAYOUT_ID)).hasSize(2);
    }

    @Test
    @DisplayName("should only touch forklifts of the given layout")
    void removeIdleInGarage_onlyGivenLayout() {
        // given
        addForklift(LAYOUT_ID);
        addForklift(OTHER_LAYOUT_ID);

        // when
        forkliftRegistry.removeIdleInGarage(LAYOUT_ID, 5);

        // then
        assertThat(forkliftRegistry.findByLayoutId(LAYOUT_ID)).isEmpty();
        assertThat(forkliftRegistry.findByLayoutId(OTHER_LAYOUT_ID)).hasSize(1);
    }
}
