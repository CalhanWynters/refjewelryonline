package com.github.calhanwynters.model.necklaceattributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.Optional;

/**
 * Domain value object representing a standardized necklace size, using length in inches as the canonical representation.
 * - Immutable record, domain-only (no infra annotations/deps).
 * - Stores length in inches (BigDecimal for precision).
 * - Provides conversions to common units (cm, inches).
 * - Encapsulates standard necklace lengths (e.g., choker, princess).
 */
public record NecklaceSizeVO(
        BigDecimal lengthInches // Canonical value: Length in inches (non-null, positive)
) {
    // Standard precision for length calculations
    private static final int LENGTH_SCALE = 2;
    private static final BigDecimal INCHES_PER_CM = new BigDecimal("0.393701");

    // Compact constructor with validation and normalization
    public NecklaceSizeVO {
        Objects.requireNonNull(lengthInches, "lengthInches must not be null");

        if (lengthInches.signum() <= 0) {
            throw new IllegalArgumentException("lengthInches must be positive");
        }

        // Normalize the internal length to a consistent scale
        lengthInches = lengthInches.setScale(LENGTH_SCALE, RoundingMode.HALF_UP);
    }

    // --- Factories ---

    /**
     * Creates a NecklaceSizeVO from length in inches.
     * @param inches The length in inches.
     * @return A new NecklaceSizeVO instance.
     */
    public static NecklaceSizeVO ofInches(BigDecimal inches) {
        return new NecklaceSizeVO(inches);
    }

    /**
     * Creates a NecklaceSizeVO from length in centimeters.
     * @param cm The length in centimeters.
     * @return A new NecklaceSizeVO instance.
     */
    public static NecklaceSizeVO ofCentimeters(BigDecimal cm) {
        BigDecimal inches = cm.multiply(INCHES_PER_CM);
        return new NecklaceSizeVO(inches);
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
        return lengthInches.divide(INCHES_PER_CM, LENGTH_SCALE, RoundingMode.HALF_UP);
    }

    /**
     * Identifies the standard industry name for the current length (e.g., "Choker", "Princess").
     * @return An Optional containing the standard name, if applicable.
     */
    public Optional<String> getStandardLengthName() {
        BigDecimal inches = lengthInches.setScale(0, RoundingMode.HALF_UP); // Round to nearest whole inch for comparison

        if (inches.compareTo(BigDecimal.valueOf(14)) <= 0) return Optional.of("Collar");
        if (inches.compareTo(BigDecimal.valueOf(16)) <= 0) return Optional.of("Choker");
        if (inches.compareTo(BigDecimal.valueOf(18)) <= 0) return Optional.of("Princess");
        if (inches.compareTo(BigDecimal.valueOf(20)) <= 0) return Optional.of("Matinee");
        if (inches.compareTo(BigDecimal.valueOf(34)) <= 0) return Optional.of("Opera");
        if (inches.compareTo(BigDecimal.valueOf(35)) > 0) return Optional.of("Rope/Lariat");

        return Optional.empty(); // For custom/uncommon lengths
    }

    // --- Comparison ---

    /**
     * Compares this necklace size to another by their length in inches.
     * @param other The other NecklaceSizeVO to compare to.
     * @return A negative integer, zero, or a positive integer as this size
     *         is less than, equal to, or greater than the specified size.
     */
    public int compareTo(NecklaceSizeVO other) {
        return this.lengthInches.compareTo(other.lengthInches);
    }
}
