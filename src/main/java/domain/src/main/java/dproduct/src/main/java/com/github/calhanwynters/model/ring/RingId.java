package com.github.calhanwynters.model.ring;

import java.util.Objects;
import java.util.UUID;

/**
 * Domain value object representing the unique identifier for a Ring aggregate.
 */
public record RingId(String value) {
    public RingId {
        Objects.requireNonNull(value, "RingId value cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("RingId value cannot be empty or blank");
        }
    }

    public static RingId generate() {
        return new RingId(UUID.randomUUID().toString());
    }

    @Override
    public String toString() {
        return value;
    }
}
