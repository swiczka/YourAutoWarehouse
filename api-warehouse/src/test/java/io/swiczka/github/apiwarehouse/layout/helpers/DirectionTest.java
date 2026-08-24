package io.swiczka.github.apiwarehouse.layout.helpers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DirectionTest {

    @ParameterizedTest
    @CsvSource({"0,UP", "1,DOWN", "2,LEFT", "3,RIGHT"})
    @DisplayName("fromValue should return correct Direction for valid values")
    void fromValue_validValues(final int value, final String expectedName) {
        // when
        Direction direction = Direction.fromValue(value);

        // then
        assertThat(direction.name()).isEqualTo(expectedName);
        assertThat(direction.getValue()).isEqualTo(value);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1,4,5,999999})
    @DisplayName("fromValue should throw for invalid values")
    void fromValue_invalidValues(final int value) {
        // when then
        assertThatThrownBy(() -> Direction.fromValue(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Incorrect direction value:");
    }

    @Test
    @DisplayName("getValue should return assigned integer value")
    void getValue_returnsAssignedValue() {
        // when/then
        assertThat(Direction.UP.getValue()).isEqualTo(0);
        assertThat(Direction.DOWN.getValue()).isEqualTo(1);
        assertThat(Direction.LEFT.getValue()).isEqualTo(2);
        assertThat(Direction.RIGHT.getValue()).isEqualTo(3);
    }
}