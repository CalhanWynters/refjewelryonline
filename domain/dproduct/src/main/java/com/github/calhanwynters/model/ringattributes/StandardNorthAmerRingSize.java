package com.github.calhanwynters.model.ringattributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

/*** Represents the specific, standardized North American (US/Canada) ring sizes available for sale in inventory.* Each enum value's diameter is its inner diameter in millimeters.* The size chart data used aligns with common industry tables.*/
public enum StandardNorthAmerRingSize {
    // Stored as approx ISO diameter in millimeters
    SIZE_4("4", new BigDecimal("14.9")),
    SIZE_4_5("4 1/2", new BigDecimal("15.3")),
    SIZE_5("5", new BigDecimal("15.7")),
    SIZE_5_5("5 1/2", new BigDecimal("16.1")),
    SIZE_6("6", new BigDecimal("16.5")),
    SIZE_6_5("6 1/2", new BigDecimal("16.9")),
    SIZE_7("7", new BigDecimal("17.3")),
    SIZE_7_5("7 1/2", new BigDecimal("17.7")),
    SIZE_8("8", new BigDecimal("18.2")),
    SIZE_8_5("8 1/2", new BigDecimal("18.6")),
    SIZE_9("9", new BigDecimal("19.0")),
    SIZE_9_5("9 1/2", new BigDecimal("19.4")),
    SIZE_10("10", new BigDecimal("19.8")),
    SIZE_10_5("10 1/2", new BigDecimal("20.2")),
    SIZE_11("11", new BigDecimal("20.6")),
    SIZE_11_5("11 1/2", new BigDecimal("21.0")),
    SIZE_12("12", new BigDecimal("21.4")),
    SIZE_12_5("12 1/2", new BigDecimal("21.8")),
    SIZE_13("13", new BigDecimal("22.2"));

    // Define the scale and PI in a static nested class to bypass initialization order rules
    private static class Constants {
        static final int LOCAL_SCALE = 2;
        static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
        static final BigDecimal PI = new BigDecimal("3.14159265358979323846");
    }

    private final String displayString;
    /** Stores the raw inner diameter input value in millimeters, scaled upon creation. */
    private final BigDecimal isoDiameterMm;

    StandardNorthAmerRingSize(String displayString, BigDecimal isoDiameterMm) {
        this.displayString = displayString;
        // Access constants safely through the nested class
        this.isoDiameterMm = isoDiameterMm.setScale(Constants.LOCAL_SCALE, Constants.ROUNDING_MODE);
    }

    public String getDisplayString() {
        return displayString;
    }

    /** Returns the internal diameter in millimeters (ISO standard), rounded to LOCAL_SCALE. */
    public BigDecimal getIsoDiameterMm() {
        return isoDiameterMm;
    }

    /** Calculates the internal circumference from the stored diameter using the precise PI value. */
    public BigDecimal getIsoCircumferenceMm() {
        // Circumference = Diameter * PI
        return this.isoDiameterMm.multiply(Constants.PI).setScale(Constants.LOCAL_SCALE, Constants.ROUNDING_MODE);
    }

    /**
     * Finds the closest standard US size enum based on a target diameter value.
     * @param targetDiameterMm The target internal diameter in millimeters (e.g., from a measurement tool).
     * @return An Optional containing the closest matching standard stock size, or empty if input is null.
     */
    public static Optional<StandardNorthAmerRingSize> findClosestStandardSizeFromDiameter(BigDecimal targetDiameterMm) {
        if (targetDiameterMm == null) {
            return Optional.empty();
        }
        // Normalize the target input for stable comparison
        final BigDecimal normalizedTarget = targetDiameterMm.setScale(Constants.LOCAL_SCALE, Constants.ROUNDING_MODE);

        return Arrays.stream(values())
                .min(Comparator.comparing(stdSize ->
                        // Compare the input diameter against the enum constant's diameter
                        normalizedTarget.subtract(stdSize.getIsoDiameterMm()).abs()
                ));
    }
}
