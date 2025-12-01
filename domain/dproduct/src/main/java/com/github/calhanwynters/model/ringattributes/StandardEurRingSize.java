package com.github.calhanwynters.model.ringattributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

/*** Represents the specific, standardized European ring sizes available for sale in inventory.* Each enum value's size number is its inner circumference in millimeters (ISO 8653 standard).*/
public enum StandardEurRingSize {
    // We store the circumference values here as BigDecimals in the constructor arguments
    SIZE_47("47", new BigDecimal("47")),
    SIZE_48("48", new BigDecimal("48")),
    SIZE_49("49", new BigDecimal("49")),
    SIZE_50("50", new BigDecimal("50")),
    SIZE_51("51", new BigDecimal("51")),
    SIZE_52("52", new BigDecimal("52")),
    SIZE_53("53", new BigDecimal("53")),
    SIZE_54("54", new BigDecimal("54")),
    SIZE_55("55", new BigDecimal("55")),
    SIZE_56("56", new BigDecimal("56")),
    SIZE_57("57", new BigDecimal("57")),
    SIZE_58("58", new BigDecimal("58")),
    SIZE_59("59", new BigDecimal("59")),
    SIZE_60("60", new BigDecimal("60")),
    SIZE_61("61", new BigDecimal("61")),
    SIZE_62("62", new BigDecimal("62")),
    SIZE_63("63", new BigDecimal("63")),
    SIZE_64("64", new BigDecimal("64")),
    SIZE_65("65", new BigDecimal("65"));

    // Add all sizes you actually stock.
    // Define the scale and PI as static fields
    private static final int LOCAL_DIAMETER_SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    // Sufficient precision for common use cases
    private static final BigDecimal PI = new BigDecimal("3.14159265358979323846");

    private final String displayString;
    /** Stores the raw inner circumference input value in millimeters. */
    private final BigDecimal circumferenceMm;

    /*** Enum constructor stores the provided circumference.* Note: Accessing static fields (like PI or LOCAL_DIAMETER_SCALE) is avoided in the constructor.*/
    StandardEurRingSize(String displayString, BigDecimal circumferenceMm) {
        this.displayString = displayString;
        this.circumferenceMm = circumferenceMm;
    }

    public String getDisplayString() {
        return displayString;
    }

    /**
     * Public getter for the raw inner circumference input value in millimeters.
     * Required for external access, e.g., in unit tests or when converting back to circumference charts.
     */
    public BigDecimal getCircumferenceMm() {
        return circumferenceMm;
    }

    /**
     * Public getter for the precise PI value used internally.
     */
    public static BigDecimal getPiValue() {
        return PI;
    }

    /*** Calculates the internal diameter on demand using the stored circumference and static PI value.*/
    public BigDecimal getIsoDiameterMm() {
        // Calculation happens here, safely after static fields are initialized.
        return this.circumferenceMm.divide(PI, LOCAL_DIAMETER_SCALE, ROUNDING_MODE);
    }

    /*** Finds the closest standard European stock size based on a measured or calculated *diameter*.** This improved method accepts a raw BigDecimal diameter, removing the dependency* on the external 'RingSizeVO' class.** @param targetDiameterMm The target internal diameter in millimeters (e.g., from a measurement tool).* @return An Optional containing the closest matching standard stock size, or empty if input is null.*/
    public static Optional<StandardEurRingSize> findClosestStandardSizeFromDiameter(BigDecimal targetDiameterMm) {
        if (targetDiameterMm == null) {
            return Optional.empty();
        }
        // Normalize the target input for stable comparison
        final BigDecimal normalizedTarget = targetDiameterMm.setScale(LOCAL_DIAMETER_SCALE, ROUNDING_MODE);

        return Arrays.stream(values()).min(Comparator.comparing(
                // Compare the input diameter against the calculated diameter of the enum constants
                stdSize -> normalizedTarget.subtract(stdSize.getIsoDiameterMm()).abs()));
    }
}
