package io.swiczka.github.apiwarehouse.exceptions;

public class WarehouseFullException extends RuntimeException {
    public WarehouseFullException(String message) {
        super(message);
    }
}
