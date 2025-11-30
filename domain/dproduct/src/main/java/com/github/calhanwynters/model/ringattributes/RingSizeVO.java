package com.github.calhanwynters.model.ringattributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.Optional;

public record RingSizeVO(
        BigDecimal diameterMm // Canonical value: Internal diameter in millimeters (non-null, positive)
) {
    // Standard precision for diameter calculations
    private static final int DIAMETER_SCALE = 2;
    // Define a small tolerance for comparing approximate sizes (inclusive check now)
    private static final BigDecimal TOLERANCE = new BigDecimal("0.01"); // Allow a 0.01 deviation

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
        // Formula: Diameter = (US Size * 0.83) + 11.5 mm
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
        // Formula: US Size = (Diameter - 11.5) / 0.83
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
     * Uses a tolerance check to handle inexact formulas.
     * @return An Optional containing the US display string if a common size is found within tolerance.
     */
    public Optional<String> toUsDisplayString() {
        BigDecimal usSize = toUsSize(); // Use the calculated US size directly

        // Use the private static helper method for comparisons
        if (isApproximately(usSize, "7.0")) return Optional.of("7");
        if (isApproximately(usSize, "7.5")) return Optional.of("7 1/2");
        if (isApproximately(usSize, "8.0")) return Optional.of("8");

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

    /**
     * Helper to check if the calculated size is within tolerance of a standard size.
     * This replaces the invalid inner method syntax.
     */
    private static boolean isApproximately(BigDecimal calculated, String standard) {
        BigDecimal standardBD = new BigDecimal(standard);
        // Check if the absolute difference is less than OR EQUAL TO the tolerance
        // compareTo returns 0 if equal, -1 if less than. We want both.
        return calculated.subtract(standardBD).abs().compareTo(TOLERANCE) <= 0;
    }
}
