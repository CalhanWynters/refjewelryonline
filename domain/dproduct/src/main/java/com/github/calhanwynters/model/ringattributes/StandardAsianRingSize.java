package com.github.calhanwynters.model.ringattributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

/**
 * Represents the specific, standardized Japanese/Chinese/HK ring sizes available for sale in inventory.
 * Each enum value maps to an exact ISO diameter (internal diameter in mm).
 * The system typically uses whole sizes, where Size 1 is 13.0mm, and each size increments by approximately 0.33mm diameter.
 */
public enum StandardAsianRingSize {
    // Note: These sizes are the internal diameter in millimeters. Common sizes are listed.
    SIZE_1("1", new BigDecimal("13.0")),
    SIZE_2("2", new BigDecimal("13.3")),
    SIZE_3("3", new BigDecimal("13.7")), // Note: charts vary slightly here, sometimes 13.66
    SIZE_4("4", new BigDecimal("14.0")),
    SIZE_5("5", new BigDecimal("14.3")),
    SIZE_6("6", new BigDecimal("14.7")),
    SIZE_7("7", new BigDecimal("15.0")),
    SIZE_8("8", new BigDecimal("15.3")),
    SIZE_9("9", new BigDecimal("15.7")),
    SIZE_10("10", new BigDecimal("16.0")),
    SIZE_11("11", new BigDecimal("16.3")),
    SIZE_12("12", new BigDecimal("16.7")),
    SIZE_13("13", new BigDecimal("17.0")),
    SIZE_14("14", new BigDecimal("17.3")),
    SIZE_15("15", new BigDecimal("17.7")),
    SIZE_16("16", new BigDecimal("18.0")),
    SIZE_17("17", new BigDecimal("18.3")),
    SIZE_18("18", new BigDecimal("18.7")),
    SIZE_19("19", new BigDecimal("19.0")),
    SIZE_20("20", new BigDecimal("19.3")),
    SIZE_21("21", new BigDecimal("19.7")),
    SIZE_22("22", new BigDecimal("20.0")),
    SIZE_23("23", new BigDecimal("20.3")),
    SIZE_24("24", new BigDecimal("20.7")),
    SIZE_25("25", new BigDecimal("21.0")),
    SIZE_26("26", new BigDecimal("21.3"));
    // ... ensure all sizes you stock are added here.

    // Define the scale and PI in a static nested class to bypass initialization order rules
    private static class Constants {
        static final int LOCAL_SCALE = 2;
        static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
        static final BigDecimal PI = new BigDecimal("3.14159265358979323846");
    }

    private final String displayString;
    /** The ISO internal diameter in millimeters. */
    private final BigDecimal isoDiameterMm;

    /**
     * Constructor for Japanese ring sizes.
     * @param displayString The string representation (e.g., "15").
     * @param isoDiameterMm The diameter value in mm.
     */
    StandardAsianRingSize(String displayString, BigDecimal isoDiameterMm) {
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
        // Circumference = Diameter * PI (Result rounded to LOCAL_SCALE)
        return this.isoDiameterMm.multiply(Constants.PI).setScale(Constants.LOCAL_SCALE, Constants.ROUNDING_MODE);
    }

    /**
     * Finds the closest standard Japanese/Chinese stock size based on a measured or calculated diameter.
     *
     * @param targetDiameterMm The target internal diameter in millimeters (e.g., 17.3mm).
     * @return An Optional containing the closest matching standard stock size, or empty if input is null.
     */
    public static Optional<StandardAsianRingSize> findClosestStandardSizeFromDiameter(BigDecimal targetDiameterMm) {
        if (targetDiameterMm == null) {
            return Optional.empty();
        }

        // Normalize the input target diameter using the static constants before comparison
        final BigDecimal normalizedTarget = targetDiameterMm.setScale(Constants.LOCAL_SCALE, Constants.ROUNDING_MODE);

        return Arrays.stream(values())
                .min(Comparator.comparing(
                        // Calculate the absolute difference between the target and the enum value's diameter
                        stdSize -> normalizedTarget.subtract(stdSize.getIsoDiameterMm()).abs()
                ));
    }
}
