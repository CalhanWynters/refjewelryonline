package com.github.calhanwynters.model.ringattributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

/**
 * Represents the specific, standardized German ring sizes available for sale in inventory.
 * Each enum value's display string is its internal diameter in millimeters (ISO 8653 standard).
 */
public enum StandardGermanRingSize {
    // Note: These sizes are the internal diameter in millimeters.
    SIZE_15_0("15.0", new BigDecimal("15.0")),
    SIZE_15_5("15.5", new BigDecimal("15.5")),
    SIZE_16_0("16.0", new BigDecimal("16.0")),
    SIZE_16_5("16.5", new BigDecimal("16.5")),
    SIZE_17_0("17.0", new BigDecimal("17.0")),
    SIZE_17_5("17.5", new BigDecimal("17.5")),
    SIZE_18_0("18.0", new BigDecimal("18.0")),
    SIZE_18_5("18.5", new BigDecimal("18.5")),
    SIZE_19_0("19.0", new BigDecimal("19.0")),
    SIZE_19_5("19.5", new BigDecimal("19.5")),
    SIZE_20_0("20.0", new BigDecimal("20.0")),
    SIZE_20_5("20.5", new BigDecimal("20.5")),
    SIZE_21_0("21.0", new BigDecimal("21.0"));
    // ... ensure all sizes you stock are added here.

    // Static fields are still fine to use within static methods, just not constructors.
    private static final int LOCAL_DIAMETER_SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    private final String displayString;
    /** The ISO internal diameter in millimeters. */
    private final BigDecimal isoDiameterMm;

    /**
     * Constructor for German ring sizes.
     * @param displayString The string representation (e.g., "17.5").
     * @param isoDiameterMm The diameter value in mm.
     */
    StandardGermanRingSize(String displayString, BigDecimal isoDiameterMm) {
        this.displayString = displayString;
        // Use literals directly in the constructor to avoid the "accessing static field from enum constructor" error:
        this.isoDiameterMm = isoDiameterMm.setScale(2, RoundingMode.HALF_UP);
    }

    public String getDisplayString() {
        return displayString;
    }

    public BigDecimal getIsoDiameterMm() {
        return isoDiameterMm;
    }

    /**
     * Finds the closest standard German stock size based on a measured or calculated diameter.
     * <p>
     * This method accepts a raw BigDecimal diameter for flexibility and reduced coupling.
     *
     * @param targetDiameterMm The target internal diameter in millimeters (e.g., 17.35mm).
     * @return An Optional containing the closest matching standard stock size, or empty if input is null.
     */
    public static Optional<StandardGermanRingSize> findClosestStandardSize(BigDecimal targetDiameterMm) {
        if (targetDiameterMm == null) {
            return Optional.empty();
        }

        // Normalize the input target diameter using the static constants before comparison
        final BigDecimal normalizedTarget = targetDiameterMm.setScale(LOCAL_DIAMETER_SCALE, ROUNDING_MODE);

        return Arrays.stream(values())
                .min(Comparator.comparing(
                        // Calculate the absolute difference between the target and the enum value's diameter
                        stdSize -> normalizedTarget.subtract(stdSize.getIsoDiameterMm()).abs()
                ));
    }
}
