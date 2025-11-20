package com.github.calhanwynters.model.earring;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.Optional;

/**
 * Domain value object representing an earring size, using diameter or length in millimeters (mm) as the canonical representation.
 * - Immutable record, domain-only (no infra annotations/deps).
 * - Stores size (length/diameter) in mm (BigDecimal for precision).
 * - Optional label to clarify the size type (e.g., "Diameter", "Drop Length", "Hoop Diameter").
 * - Validation ensures non-negative size.
 */
public record EarringSizeVO(
        BigDecimal sizeMm, // Canonical value: size in millimeters (non-null, positive)
        String label       // Optional label for clarity (e.g., "Diameter")
) {
    // Standard precision for size calculations
    private static final int SIZE_SCALE = 2;
    private static final BigDecimal INCHES_PER_MM = new BigDecimal("0.0393701");

    // Compact constructor with validation and normalization
    public EarringSizeVO {
        Objects.requireNonNull(sizeMm, "sizeMm must not be null");

        if (sizeMm.signum() <= 0) {
            throw new IllegalArgumentException("sizeMm must be positive");
        }

        // Normalize the internal size to a consistent scale
        sizeMm = sizeMm.setScale(SIZE_SCALE, RoundingMode.HALF_UP).stripTrailingZeros();
        label = normalizeLabel(label);
    }

    // --- Factories ---

    /**
     * Creates an EarringSizeVO from size in millimeters.
     * @param mm The size in millimeters.
     * @return A new EarringSizeVO instance.
     */
    public static EarringSizeVO ofMillimeters(BigDecimal mm) {
        return new EarringSizeVO(mm, null);
    }

    /**
     * Creates an EarringSizeVO from size in millimeters with a specific label.
     * @param mm The size in millimeters.
     * @param label The descriptive label (e.g., "Diameter", "Drop Length").
     * @return A new EarringSizeVO instance.
     */
    public static EarringSizeVO ofMillimeters(BigDecimal mm, String label) {
        return new EarringSizeVO(mm, label);
    }

    /**
     * Creates an EarringSizeVO from size in inches.
     * @param inches The size in inches.
     * @return A new EarringSizeVO instance.
     */
    public static EarringSizeVO ofInches(BigDecimal inches) {
        BigDecimal mm = inches.divide(INCHES_PER_MM, SIZE_SCALE, RoundingMode.HALF_UP);
        return new EarringSizeVO(mm, null);
    }

    // --- Accessors/Conversions ---

    /**
     * Gets the size in millimeters.
     * @return The size in millimeters.
     */
    public BigDecimal inMillimeters() {
        return sizeMm;
    }

    /**
     * Gets the size in inches.
     * @return The size in inches.
     */
    public BigDecimal inInches() {
        return sizeMm.multiply(INCHES_PER_MM).setScale(SIZE_SCALE, RoundingMode.HALF_UP).stripTrailingZeros();
    }

    /**
     * Provides the optional label describing the measurement type.
     * @return An Optional containing the label, if present.
     */
    public Optional<String> labelOptional() {
        return Optional.ofNullable(label);
    }

    // --- Comparison ---

    /**
     * Compares this earring size to another by their millimeter size.
     * @param other The other EarringSizeVO to compare to.
     * @return A negative integer, zero, or a positive integer as this size
     *         is less than, equal to, or greater than the specified size.
     */
    public int compareTo(EarringSizeVO other) {
        return this.sizeMm.compareTo(other.sizeMm);
    }

    // --- Helpers ---
    private static String normalizeLabel(String label) {
        if (label == null) return null;
        String t = label.strip();
        return t.isEmpty() ? null : t;
    }
}
