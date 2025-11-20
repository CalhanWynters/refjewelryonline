package com.github.calhanwynters.model.necklace;

import java.util.Objects;
import java.util.UUID;

/**
 * Domain value object representing the unique identifier for a Necklace aggregate.
 */
public record NecklaceId(String value) {
    public NecklaceId {
        Objects.requireNonNull(value, "NecklaceId value cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("NecklaceId value cannot be empty or blank");
        }
    }

    public static NecklaceId generate() {
        return new NecklaceId(UUID.randomUUID().toString());
    }

    @Override
    public String toString() {
        return value;
    }
}
