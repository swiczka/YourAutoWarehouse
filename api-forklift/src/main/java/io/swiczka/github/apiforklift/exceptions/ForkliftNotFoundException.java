package io.swiczka.github.apiforklift.exceptions;

public class ForkliftNotFoundException extends RuntimeException {
    public ForkliftNotFoundException(String message) {
        super(message);
    }
}
