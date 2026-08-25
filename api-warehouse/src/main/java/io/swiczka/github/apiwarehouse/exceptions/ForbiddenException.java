package io.swiczka.github.apiwarehouse.exceptions;

public class ForbiddenException extends RuntimeException {

    public ForbiddenException(final String message) {
        super(message);
    }
}
