package io.swiczka.github.apigateway.helpers;

import java.security.Principal;

public record GuestPrincipal(String guestId) implements Principal {
    @Override
    public String getName() {
        return guestId;
    }
}