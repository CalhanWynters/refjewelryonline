package com.github.calhanwynters.model.ringattributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

public enum StandardNorthAmerRingSize {
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
    // Add more as needed

    // Define the scale locally within the enum to avoid access issues
    private static final int LOCAL_DIAMETER_SCALE = 2;

    private final String displayString;
    private final BigDecimal isoDiameterMm;

    StandardNorthAmerRingSize(String displayString, BigDecimal isoDiameterMm) {
        this.displayString = displayString;
        // Use the local scale definition
        this.isoDiameterMm = isoDiameterMm.setScale(LOCAL_DIAMETER_SCALE, RoundingMode.HALF_UP);
    }

    public String getDisplayString() {
        return displayString;
    }

    public BigDecimal getIsoDiameterMm() {
        return isoDiameterMm;
    }

    /**
     * Finds the closest standard US size enum based on a calculated RingSizeVO diameter.
     * @param targetSize The size measured or converted using the continuous model (expects the ISO diameter via toIsoSize()).
     * @return The closest matching standard stock size.
     */
    public static Optional<StandardNorthAmerRingSize> findClosestStandardSize(RingSizeVO targetSize) {
        if (targetSize == null) return Optional.empty();

        return Arrays.stream(values())
                .min(Comparator.comparing(stdSize ->
                        targetSize.toIsoSize().subtract(stdSize.getIsoDiameterMm()).abs()
                ));
    }
}
