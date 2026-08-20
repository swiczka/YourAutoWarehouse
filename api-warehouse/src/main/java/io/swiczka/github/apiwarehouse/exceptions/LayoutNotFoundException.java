package io.swiczka.github.apiwarehouse.exceptions;

import java.util.UUID;

public class LayoutNotFoundException extends RuntimeException {
    public LayoutNotFoundException(final UUID operatorId) {
        super("No layout found for operator with Id " + operatorId);
    }

    public LayoutNotFoundException(final Long id) {
        super("No layout found with Id " + id);
    }
}
