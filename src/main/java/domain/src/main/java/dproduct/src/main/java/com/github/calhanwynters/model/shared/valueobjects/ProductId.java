package com.github.calhanwynters.model.shared.valueobjects;

import java.util.Objects;
import java.util.UUID;

/**
 * Domain value object representing the unique identifier for a Product aggregate.
 */
public record ProductId(String value) {
    public ProductId {
        Objects.requireNonNull(value, "ProductId value cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("ProductId value cannot be empty or blank");
        }
    }

    public static com.github.calhanwynters.model.shared.valueobjects.ProductId generate() {
        return new com.github.calhanwynters.model.shared.valueobjects.ProductId(UUID.randomUUID().toString());
    }

    @Override
    public String toString() {
        return value;
    }
}