package com.github.calhanwynters.model.shared.valueobjects;

import java.util.Objects;

/**
 * Domain value object for product descriptions.
 * Encapsulates validation and normalization rules for a description string.
 */
public record DescriptionVO(String value) {
    private static final int MIN_LENGTH = 10;
    private static final int MAX_LENGTH = 2000;

    // Compact constructor with validation and normalization
    public DescriptionVO {
        Objects.requireNonNull(value, "Description value cannot be null");
        String stripped = value.strip();

        if (stripped.length() < MIN_LENGTH) {
            throw new IllegalArgumentException("Description must be at least " + MIN_LENGTH + " characters long.");
        }

        if (stripped.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Description cannot exceed " + MAX_LENGTH + " characters.");
        }

        // Ensure the internal value is clean
        value = stripped;
    }

    // Example domain behavior
    public DescriptionVO truncate(int maxLength) {
        if (value.length() <= maxLength) {
            return this;
        }
        // Return a new value object with the truncated string
        String truncated = value.substring(0, maxLength - 3) + "...";
        return new DescriptionVO(truncated);
    }

    // You can also add a factory method if you prefer:
    public static DescriptionVO of(String description) {
        return new DescriptionVO(description);
    }
}
