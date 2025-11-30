package com.github.calhanwynters.model.shared.valueobjects;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * A Value Object (VO) enum for handling different weight units and their conversions.
 * It uses BigDecimal for precise arithmetic, suitable for financial or inventory systems.
 */
public enum WeightUnitVO {
    /** Represents the base unit of a Gram. */
    GRAM {
        @Override
        public BigDecimal toGrams(BigDecimal v) {
            Objects.requireNonNull(v, "Value must not be null");
            if (v.signum() < 0) {
                throw new IllegalArgumentException("Value must not be negative");
            }
            return v;
        }

        @Override
        public BigDecimal fromGrams(BigDecimal g) {
            Objects.requireNonNull(g, "Grams must not be null");
            if (g.signum() < 0) {
                throw new IllegalArgumentException("Grams must not be negative");
            }
            return g;
        }
    },
    /** Represents the unit of an Avoirdupois Ounce (general purpose ounce). */
    OUNCE {
        @Override
        public BigDecimal toGrams(BigDecimal v) {
            Objects.requireNonNull(v, "Value must not be null");
            if (v.signum() < 0) {
                throw new IllegalArgumentException("Value must not be negative");
            }
            return v.multiply(GRAMS_PER_OUNCE, MC);
        }

        @Override
        public BigDecimal fromGrams(BigDecimal g) {
            Objects.requireNonNull(g, "Grams must not be null");
            if (g.signum() < 0) {
                throw new IllegalArgumentException("Grams must not be negative");
            }
            return g.divide(GRAMS_PER_OUNCE, SCALE, RoundingMode.HALF_UP).stripTrailingZeros();
        }
    },
    /** Represents the unit of a Carat (used for gemstones). */
    CARAT {
        @Override
        public BigDecimal toGrams(BigDecimal v) {
            Objects.requireNonNull(v, "Value must not be null");
            if (v.signum() < 0) {
                throw new IllegalArgumentException("Value must not be negative");
            }
            return v.multiply(GRAMS_PER_CARAT, MC);
        }

        @Override
        public BigDecimal fromGrams(BigDecimal g) {
            Objects.requireNonNull(g, "Grams must not be null");
            if (g.signum() < 0) {
                throw new IllegalArgumentException("Grams must not be negative");
            }
            return g.divide(GRAMS_PER_CARAT, SCALE, RoundingMode.HALF_UP).stripTrailingZeros();
        }
    },
    /** Represents the unit of a Troy Ounce (used for precious metals). */
    TROY_OUNCE {
        @Override
        public BigDecimal toGrams(BigDecimal v) {
            Objects.requireNonNull(v, "Value must not be null");
            if (v.signum() < 0) {
                throw new IllegalArgumentException("Value must not be negative");
            }
            return v.multiply(GRAMS_PER_TROY_OUNCE, MC);
        }

        @Override
        public BigDecimal fromGrams(BigDecimal g) {
            Objects.requireNonNull(g, "Grams must not be null");
            if (g.signum() < 0) {
                throw new IllegalArgumentException("Grams must not be negative");
            }
            return g.divide(GRAMS_PER_TROY_OUNCE, SCALE, RoundingMode.HALF_UP).stripTrailingZeros();
        }
    };

    // Constants for conversion factors
    private static final BigDecimal GRAMS_PER_OUNCE = new BigDecimal("28.349523125");
    private static final BigDecimal GRAMS_PER_CARAT = new BigDecimal("0.2");
    private static final BigDecimal GRAMS_PER_TROY_OUNCE = new BigDecimal("31.1034768"); // Standard for precious metals

    // Scale and MathContext for precision
    private static final int SCALE = 8; // Preserves sub-milligram precision (0.00000001 g)
    private static final MathContext MC = new MathContext(16, RoundingMode.HALF_UP);

    /**
     * Converts a value in this unit to grams.
     *
     * @param value The value in the current unit.
     * @return The value in grams.
     * @throws IllegalArgumentException if the value is null or negative.
     */
    public abstract BigDecimal toGrams(BigDecimal value);

    /**
     * Converts a value in grams to this unit.
     *
     * @param grams The value in grams.
     * @return The value in the current unit, rounded to the defined SCALE.
     * @throws IllegalArgumentException if the grams value is null or negative.
     */
    public abstract BigDecimal fromGrams(BigDecimal grams);

    /**
     * Converts a given value from the current unit to a specified target unit.
     *
     * @param value The value in the current unit (this).
     * @param targetUnit The desired unit for the result.
     * @return The converted value in the target unit.
     * @throws IllegalArgumentException if the value is null or negative.
     */
    public BigDecimal convertValueTo(BigDecimal value, WeightUnitVO targetUnit) {
        Objects.requireNonNull(value, "Value must not be null");
        if (value.signum() < 0) {
            throw new IllegalArgumentException("Value must not be negative");
        }

        // Convert to intermediate grams using high precision internally
        BigDecimal grams = this.toGrams(value);

        // Convert from grams to the target unit, applying final rounding/scaling
        return targetUnit.fromGrams(grams);
    }
}
