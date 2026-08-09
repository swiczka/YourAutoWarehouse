package io.swiczka.github.apiwarehouse.layout.helpers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Direction{
    UP(0), DOWN(1), LEFT(2), RIGHT(3);

    private final int value;

    Direction(int value){
        this.value = value;
    }

    @JsonValue
    public int getValue() {
        return value;
    }

    @JsonCreator
    public static Direction fromValue(int value) {
        for (Direction direction : Direction.values()) {
            if (direction.value == value) {
                return direction;
            }
        }

        throw new IllegalArgumentException("Incorrect direction value: " + value);
    }
}

