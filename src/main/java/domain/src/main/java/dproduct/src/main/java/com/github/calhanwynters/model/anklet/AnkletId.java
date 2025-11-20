package com.github.calhanwynters.model.anklet;

import java.util.Objects;
import java.util.UUID; // Add this import

/**
 * Domain value object representing the unique identifier for an Anklet aggregate.
 * Provides type safety and enforces non-null, non-empty identity.
 */
public record AnkletId(String value) {
    // Compact constructor for validation
    public AnkletId {
        Objects.requireNonNull(value, "AnkletId value cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("AnkletId value cannot be empty or blank");
        }
    }

    /**
     * Creates a new AnkletId with a randomly generated UUID.
     * @return A new AnkletId instance.
     */
    public static AnkletId generate() {
        return new AnkletId(UUID.randomUUID().toString());
    }

    @Override
    public String toString() {
        return value;
    }
}
