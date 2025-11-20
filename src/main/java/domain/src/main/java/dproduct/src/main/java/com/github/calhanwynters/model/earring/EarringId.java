package com.github.calhanwynters.model.earring;

import java.util.Objects;
import java.util.UUID;

/**
 * Domain value object representing the unique identifier for an Earring aggregate.
 */
public record EarringId(String value) {
    public EarringId {
        Objects.requireNonNull(value, "EarringId value cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("EarringId value cannot be empty or blank");
        }
    }

    public static EarringId generate() {
        return new EarringId(UUID.randomUUID().toString());
    }

    @Override
    public String toString() {
        return value;
    }
}
