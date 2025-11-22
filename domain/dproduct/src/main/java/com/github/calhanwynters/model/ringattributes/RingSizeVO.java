package com.github.calhanwynters.model.ringattributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.Optional;

/**
 * Domain value object representing a standardized ring size, using internal diameter in mm as the canonical representation.
 * - Immutable record, domain-only (no infra annotations/deps).
 * - Stores diameter in mm (BigDecimal for precision).
 * - Factories handle different sizing systems (US, UK/Aus, ISO).
 * - Provides conversions and comparison operations.
 */
public record RingSizeVO(
        BigDecimal diameterMm // Canonical value: Internal diameter in millimeters (non-null, positive)
) {
    // Standard precision for diameter calculations
    private static final int DIAMETER_SCALE = 2;

    // Compact constructor with validation and normalization
    public RingSizeVO {
        Objects.requireNonNull(diameterMm, "diameterMm must not be null");

        if (diameterMm.signum() <= 0) {
            throw new IllegalArgumentException("diameterMm must be positive");
        }

        // Normalize the internal diameter to a consistent scale
        diameterMm = diameterMm.setScale(DIAMETER_SCALE, RoundingMode.HALF_UP);
    }

    // --- Factories (Examples for common systems) ---

    /**
     * Creates a RingSizeVO from the ISO Standard (internal diameter in mm).
     * @param diameterMm The internal diameter in millimeters.
     * @return A new RingSizeVO instance.
     */
    public static RingSizeVO ofIsoDiameter(BigDecimal diameterMm) {
        return new RingSizeVO(diameterMm);
    }

    /**
     * Creates a RingSizeVO from the US/Canada numerical size (e.g., 6.5, 9.0).
     * @param usSize The US numerical size.
     * @return A new RingSizeVO instance.
     */
    public static RingSizeVO ofUsSize(BigDecimal usSize) {
        // Formula: Diameter = (US Size * 0.83) + 11.5 mm [4]
        BigDecimal diameter = usSize.multiply(new BigDecimal("0.83"))
                .add(new BigDecimal("11.5"));
        return new RingSizeVO(diameter);
    }

    // --- Conversions ---

    /**
     * Converts the internal diameter to an approximate US/Canada numerical size.
     * @return The approximate US size as a BigDecimal.
     */
    public BigDecimal toUsSize() {
        // Formula: US Size = (Diameter - 11.5) / 0.83 [4]
        return diameterMm.subtract(new BigDecimal("11.5"))
                .divide(new BigDecimal("0.83"), DIAMETER_SCALE, RoundingMode.HALF_UP);
    }

    /**
     * Provides the ISO standard size, which is the internal diameter in millimeters.
     * @return The diameter in mm.
     */
    public BigDecimal toIsoSize() {
        return diameterMm;
    }

    /**
     * Attempts to find the approximate official US alphabetical representation (e.g., "7", "7 1/2").
     * This is a display helper and might not cover all half/quarter sizes perfectly.
     * @return An Optional containing the US display string if a common size is found.
     */
    public Optional<String> toUsDisplayString() {
        // A simple lookup for common sizes based on calculated US numerical size
        BigDecimal usSize = toUsSize().setScale(1, RoundingMode.HALF_UP);
        if (usSize.compareTo(new BigDecimal("7.0")) == 0) return Optional.of("7");
        if (usSize.compareTo(new BigDecimal("7.5")) == 0) return Optional.of("7 1/2");
        if (usSize.compareTo(new BigDecimal("8.0")) == 0) return Optional.of("8");
        // ... add more common sizes as needed ...
        return Optional.empty();
    }

    // --- Comparison ---

    /**
     * Compares this ring size to another by their internal diameters.
     * @param other The other RingSizeVO to compare to.
     * @return A negative integer, zero, or a positive integer as this size
     *         is less than, equal to, or greater than the specified size.
     */
    public int compareTo(RingSizeVO other) {
        return this.diameterMm.compareTo(other.diameterMm);
    }
}
