package com.github.calhanwynters.model.shared.valueobjects;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * A Value Object (VO) enum for handling different weight units and their conversions.
 * It uses BigDecimal for precise arithmetic, suitable for financial or inventory systems.
 */
public enum WeightUnitVO {
    /** Represents the base unit of a Gram. */
    GRAM {
        @Override
        public BigDecimal toGrams(BigDecimal v) {
            return v;
        }

        @Override
        public BigDecimal fromGrams(BigDecimal g) {
            return g;
        }
    },
    /** Represents the unit of an Avoirdupois Ounce (general purpose ounce). */
    OUNCE {
        @Override
        public BigDecimal toGrams(BigDecimal v) {
            return v.multiply(GRAMS_PER_OUNCE, MC);
        }

        @Override
        public BigDecimal fromGrams(BigDecimal g) {
            // Apply scale and rounding for presentation/storage after conversion
            return g.divide(GRAMS_PER_OUNCE, SCALE, RoundingMode.HALF_UP).stripTrailingZeros();
        }
    },
    /** Represents the unit of a Carat (used for gemstones). */
    CARAT {
        @Override
        public BigDecimal toGrams(BigDecimal v) {
            return v.multiply(GRAMS_PER_CARAT, MC);
        }

        @Override
        public BigDecimal fromGrams(BigDecimal g) {
            // Apply scale and rounding for presentation/storage after conversion
            return g.divide(GRAMS_PER_CARAT, SCALE, RoundingMode.HALF_UP).stripTrailingZeros();
        }
    },
    /** Represents the unit of a Troy Ounce (used for precious metals). */
    TROY_OUNCE {
        @Override
        public BigDecimal toGrams(BigDecimal v) {
            return v.multiply(GRAMS_PER_TROY_OUNCE, MC);
        }

        @Override
        public BigDecimal fromGrams(BigDecimal g) {
            return g.divide(GRAMS_PER_TROY_OUNCE, SCALE, RoundingMode.HALF_UP).stripTrailingZeros();
        }
    };

    // Constants with string constructors for exact values to ensure precision
    private static final BigDecimal GRAMS_PER_OUNCE = new BigDecimal("28.349523125");
    private static final BigDecimal GRAMS_PER_CARAT = new BigDecimal("0.2");
    private static final BigDecimal GRAMS_PER_TROY_OUNCE = new BigDecimal("31.1034768"); // Standard for precious metals

    // Defines the scale (decimal places) for final converted results
    private static final int SCALE = 8; // preserves sub-milligram precision (0.00000001 g)
    // MathContext for internal high-precision multiplication operations
    private static final MathContext MC = new MathContext(16, RoundingMode.HALF_UP);

    /**
     * Converts a value in this unit to grams.
     *
     * @param value The value in the current unit.
     * @return The value in grams.
     */
    public abstract BigDecimal toGrams(BigDecimal value);

    /**
     * Converts a value in grams to this unit.
     *
     * @param grams The value in grams.
     * @return The value in the current unit, rounded to the defined SCALE.
     */
    public abstract BigDecimal fromGrams(BigDecimal grams);

    /**
     * Converts a given value from the current unit to a specified target unit.
     *
     * @param value The value in the current unit (this).
     * @param targetUnit The desired unit for the result.
     * @return The converted value in the target unit.
     */
    public BigDecimal convertValueTo(BigDecimal value, WeightUnitVO targetUnit) {
        if (this == targetUnit) {
            return value;
        }
        // Convert to intermediate grams using high precision internally
        BigDecimal grams = this.toGrams(value);

        // Convert from grams to the target unit, applying final rounding/scaling
        return targetUnit.fromGrams(grams);
    }
}
