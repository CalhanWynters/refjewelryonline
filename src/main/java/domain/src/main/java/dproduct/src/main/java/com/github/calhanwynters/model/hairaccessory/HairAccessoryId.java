package com.github.calhanwynters.model.hairaccessory;

import java.util.Objects;
import java.util.UUID;

/**
 * Domain value object representing the unique identifier for a HairAccessory aggregate.
 */
public record HairAccessoryId(String value) {
    public HairAccessoryId {
        Objects.requireNonNull(value, "HairAccessoryId value cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("HairAccessoryId value cannot be empty or blank");
        }
    }

    public static HairAccessoryId generate() {
        return new HairAccessoryId(UUID.randomUUID().toString());
    }

    @Override
    public String toString() {
        return value;
    }
}
