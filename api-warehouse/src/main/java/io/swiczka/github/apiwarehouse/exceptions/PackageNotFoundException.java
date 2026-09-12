package io.swiczka.github.apiwarehouse.exceptions;

public class PackageNotFoundException extends RuntimeException {
    public PackageNotFoundException(Long packageId) {
        super(String.format("Package with ID: %d not found", packageId));
    }
}
