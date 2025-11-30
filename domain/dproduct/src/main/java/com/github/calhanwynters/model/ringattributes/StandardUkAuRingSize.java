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

    // Define the scale locally within the enum for consistency
    private static final int LOCAL_DIAMETER_SCALE = 2;

    private final String displayString;
    private final BigDecimal isoDiameterMm;

    StandardUkAuRingSize(String displayString, BigDecimal isoDiameterMm) {
        this.displayString = displayString;
        this.isoDiameterMm = isoDiameterMm.setScale(LOCAL_DIAMETER_SCALE, RoundingMode.HALF_UP);
    }

    public String getDisplayString() {
        return displayString;
    }

    public BigDecimal getIsoDiameterMm() {
        return isoDiameterMm;
    }

    /**
     * Finds the closest standard UK/AU size enum based on a calculated RingSizeVO diameter.
     * @param targetSize The size measured or converted using the continuous model.
     * @return The closest matching standard stock size.
     */
    public static Optional<StandardUkAuRingSize> findClosestStandardSize(RingSizeVO targetSize) {
        if (targetSize == null) return Optional.empty();

        return Arrays.stream(values())
                .min(Comparator.comparing(
                        stdSize -> targetSize.toIsoSize().subtract(stdSize.getIsoDiameterMm()).abs()
                ));
    }
}
