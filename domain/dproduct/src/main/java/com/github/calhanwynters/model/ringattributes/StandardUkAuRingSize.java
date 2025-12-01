package com.github.calhanwynters.model.ringattributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

/**
 * Represents the specific, standardized UK/Australia (and Ireland/NZ/SA)
 * alphabetical ring sizes available for sale in inventory.
 * Each enum value maps to an exact ISO diameter (internal diameter in mm).
 */
public enum StandardUkAuRingSize {
    // A few common female sizes are listed here. Add all sizes you actually stock.
    SIZE_F("F", new BigDecimal("14.1")),
    SIZE_F_HALF("F 1/2", new BigDecimal("14.3")),
    SIZE_G("G", new BigDecimal("14.5")),
    SIZE_G_HALF("G 1/2", new BigDecimal("14.7")),
    SIZE_H("H", new BigDecimal("14.9")),
    SIZE_H_HALF("H 1/2", new BigDecimal("15.1")),
    SIZE_I("I", new BigDecimal("15.2")),
    SIZE_I_HALF("I 1/2", new BigDecimal("15.4")),
    SIZE_J("J", new BigDecimal("15.6")),
    SIZE_J_HALF("J 1/2", new BigDecimal("15.8")),
    SIZE_K("K", new BigDecimal("16.0")),
    SIZE_K_HALF("K 1/2", new BigDecimal("16.2")),
    SIZE_L("L", new BigDecimal("16.4")),
    SIZE_L_HALF("L 1/2", new BigDecimal("16.6")),
    SIZE_M("M", new BigDecimal("16.8")),
    SIZE_M_HALF("M 1/2", new BigDecimal("17.0")),
    SIZE_N("N", new BigDecimal("17.2")),
    SIZE_N_HALF("N 1/2", new BigDecimal("17.4")),
    SIZE_O("O", new BigDecimal("17.6")),
    SIZE_O_HALF("O 1/2", new BigDecimal("17.8")),
    SIZE_P("P", new BigDecimal("18.0")),
    SIZE_P_HALF("P 1/2", new BigDecimal("18.2")),
    SIZE_Q("Q", new BigDecimal("18.4")),
    SIZE_Q_HALF("Q 1/2", new BigDecimal("18.6")),
    SIZE_R("R", new BigDecimal("18.8")),
    SIZE_R_HALF("R 1/2", new BigDecimal("19.0")),
    // ... add typical men's sizes through Z+ (e.g., SIZE_T, SIZE_V, SIZE_Z)
    ;

    // Define the scale and PI in a static nested class to bypass initialization order rules
    private static class Constants {
        static final int LOCAL_SCALE = 2;
        static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
        static final BigDecimal PI = new BigDecimal("3.14159265358979323846");
    }

    private final String displayString;
    private final BigDecimal isoDiameterMm;

    StandardUkAuRingSize(String displayString, BigDecimal isoDiameterMm) {
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
     * Finds the closest standard UK/AU size enum based on a target diameter value.
     * The method signature was updated to accept a raw BigDecimal diameter input,
     * removing the dependency on the external 'RingSizeVO' class.
     *
     * @param targetDiameterMm The target internal diameter in millimeters (e.g., from a measurement tool).
     * @return An Optional containing the closest matching standard stock size, or empty if input is null.
     */
    public static Optional<StandardUkAuRingSize> findClosestStandardSizeFromDiameter(BigDecimal targetDiameterMm) {
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
