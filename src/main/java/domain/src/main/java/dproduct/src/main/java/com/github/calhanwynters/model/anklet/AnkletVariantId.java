package com.github.calhanwynters.model.anklet;

import java.util.Objects;
import java.util.UUID;

public record AnkletVariantId(String value) {
    public AnkletVariantId {
        Objects.requireNonNull(value, "AnkletVariantId value cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("AnkletVariantId value cannot be empty or blank");
        }
    }

    public static AnkletVariantId generate() {
        return new AnkletVariantId(UUID.randomUUID().toString());
    }

    @Override
    public String toString() {
        return value;
    }
}
