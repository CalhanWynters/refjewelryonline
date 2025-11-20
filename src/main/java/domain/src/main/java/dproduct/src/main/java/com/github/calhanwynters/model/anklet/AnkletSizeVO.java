package com.github.calhanwynters.model.anklet;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.Optional;

/**
 * Domain value object representing an anklet size, using length in inches as the canonical representation.
 * - Immutable record, domain-only (no infra annotations/deps).
 * - Stores length in inches (BigDecimal for precision).
 * - Provides conversions to common units (cm, inches).
 * - Encapsulates standard anklet lengths.
 */
public record AnkletSizeVO(
        BigDecimal lengthInches // Canonical value: Length in inches (non-null, positive)
) {
    // Standard precision for length calculations
    private static final int LENGTH_SCALE = 2;
    private static final BigDecimal INCHES_PER_CM = new BigDecimal("0.393701");

    // Compact constructor with validation and normalization
    public AnkletSizeVO {
        Objects.requireNonNull(lengthInches, "lengthInches must not be null");

        if (lengthInches.signum() <= 0) {
            throw new IllegalArgumentException("lengthInches must be positive");
        }

        // Normalize the internal length to a consistent scale
        lengthInches = lengthInches.setScale(LENGTH_SCALE, RoundingMode.HALF_UP).stripTrailingZeros();
    }

    // --- Factories ---

    /**
     * Creates an AnkletSizeVO from length in inches.
     * @param inches The length in inches.
     * @return A new AnkletSizeVO instance.
     */
    public static AnkletSizeVO ofInches(BigDecimal inches) {
        return new AnkletSizeVO(inches);
    }

    /**
     * Creates an AnkletSizeVO from length in centimeters.
     * @param cm The length in centimeters.
     * @return A new AnkletSizeVO instance.
     */
    public static AnkletSizeVO ofCentimeters(BigDecimal cm) {
        BigDecimal inches = cm.multiply(INCHES_PER_CM).setScale(LENGTH_SCALE, RoundingMode.HALF_UP);
        return new AnkletSizeVO(inches);
    }

    // --- Conversions ---

    /**
     * Gets the length in inches.
     * @return The length in inches.
     */
    public BigDecimal inInches() {
        return lengthInches;
    }

    /**
     * Gets the length in centimeters.
     * @return The length in centimeters.
     */
    public BigDecimal inCentimeters() {
        return lengthInches.divide(INCHES_PER_CM, LENGTH_SCALE, RoundingMode.HALF_UP).stripTrailingZeros();
    }

    /**
     * Identifies the standard industry name for the current length (e.g., "Standard", "Long").
     * @return An Optional containing the standard name, if applicable.
     */
    public Optional<String> getStandardLengthName() {
        BigDecimal inches = lengthInches.stripTrailingZeros();

        if (inches.compareTo(BigDecimal.valueOf(9)) <= 0) return Optional.of("Petite");
        if (inches.compareTo(BigDecimal.valueOf(10)) <= 0) return Optional.of("Standard");
        if (inches.compareTo(BigDecimal.valueOf(11)) <= 0) return Optional.of("Large");
        if (inches.compareTo(BigDecimal.valueOf(11)) > 0) return Optional.of("Extra Large");

        return Optional.empty(); // For custom/uncommon lengths
    }

    // --- Comparison ---

    /**
     * Compares this anklet size to another by their length in inches.
     * @param other The other AnkletSizeVO to compare to.
     * @return A negative integer, zero, or a positive integer as this size
     *         is less than, equal to, or greater than the specified size.
     */
    public int compareTo(AnkletSizeVO other) {
        return this.lengthInches.compareTo(other.lengthInches);
    }
}
