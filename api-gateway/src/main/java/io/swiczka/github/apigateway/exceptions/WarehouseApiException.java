package io.swiczka.github.apigateway.exceptions;

public class WarehouseApiException extends RuntimeException {

    public WarehouseApiException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
