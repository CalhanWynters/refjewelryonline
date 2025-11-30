package com.github.calhanwynters.model.shared.valueobjects;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Domain value object representing a product weight.
 * This record ensures immutability, validation, and standard weight operations.
 */
public record WeightVO(
        BigDecimal amount,
        WeightUnit unit
) implements Comparable<WeightVO> {

    // Centralized constant for maximum allowed weight in grams (e.g., 100 kg)
    private static final BigDecimal MAX_GRAMS = new BigDecimal("100000.0");
    private static final String AMOUNT_CANNOT_BE_NULL = "Amount must not be null";
    private static final String UNIT_CANNOT_BE_NULL = "Unit must not be null";
    private static final String AMOUNT_CANNOT_BE_NEGATIVE = "Amount must not be negative";
    private static final String EXCEEDED_MAX_WEIGHT = "Amount exceeds maximum allowed weight";

    // Compact constructor for validation and normalization
    public WeightVO {
        Objects.requireNonNull(amount, AMOUNT_CANNOT_BE_NULL);
        Objects.requireNonNull(unit, UNIT_CANNOT_BE_NULL);
        if (amount.signum() < 0) {
            throw new IllegalArgumentException(AMOUNT_CANNOT_BE_NEGATIVE);
        }
        // Normalize the input amount using the unit's defined scale and rounding
        amount = amount.setScale(WeightUnit.SCALE, WeightUnit.ROUNDING_MODE).stripTrailingZeros();

        // Validate total weight against maximum allowed
        if (unit.toGrams(amount).compareTo(MAX_GRAMS) > 0) {
            throw new IllegalArgumentException(EXCEEDED_MAX_WEIGHT);
        }
    }

    // Factory methods
    public static WeightVO ofGrams(BigDecimal grams) {
        return new WeightVO(grams, WeightUnit.GRAM);
    }

    public static WeightVO ofOunces(BigDecimal ounces) {
        return new WeightVO(ounces, WeightUnit.OUNCE);
    }

    public static WeightVO ofTroyOunces(BigDecimal troyOunces) {
        return new WeightVO(troyOunces, WeightUnit.TROY_OUNCE);
    }

    public static WeightVO ofCarats(BigDecimal carats) {
        return new WeightVO(carats, WeightUnit.CARAT);
    }

    // Convert to grams
    public BigDecimal inGrams() {
        return unit.toGrams(amount)
                .setScale(WeightUnit.SCALE, WeightUnit.ROUNDING_MODE)
                .stripTrailingZeros();
    }

    /**
     * Converts this weight value into a new WeightVO represented in the target unit.
     *
     * @param targetUnit The desired output unit.
     * @return A new WeightVO in the target unit.
     */
    public WeightVO toUnit(WeightUnit targetUnit) {
        if (this.unit.equals(targetUnit)) return this;
        BigDecimal resultInTargetUnit = targetUnit.fromGrams(this.inGrams());
        return new WeightVO(resultInTargetUnit, targetUnit);
    }

    // Domain operations
    public WeightVO add(WeightVO other) {
        Objects.requireNonNull(other, "Other WeightVO must not be null");
        BigDecimal totalGrams = this.inGrams().add(other.inGrams());

        if (totalGrams.compareTo(MAX_GRAMS) > 0) {
            throw new IllegalArgumentException(EXCEEDED_MAX_WEIGHT);
        }

        return WeightVO.ofGrams(totalGrams);
    }

    public WeightVO subtract(WeightVO other) {
        Objects.requireNonNull(other, "Other WeightVO must not be null");
        BigDecimal resultGrams = this.inGrams().subtract(other.inGrams());

        if (resultGrams.signum() < 0) {
            throw new IllegalArgumentException("Resulting weight must not be negative");
        }

        return WeightVO.ofGrams(resultGrams);
    }

    @Override
    public int compareTo(WeightVO other) {
        return this.inGrams().compareTo(other.inGrams());
    }

    /**
     * Enum for supported weight units using BigDecimal for precision.
     */
    public enum WeightUnit {
        GRAM(BigDecimal.ONE),
        OUNCE(new BigDecimal("28.349523125")),
        TROY_OUNCE(new BigDecimal("31.1034768")),
        CARAT(new BigDecimal("0.2"));

        public static final int SCALE = 4;
        public static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

        private final BigDecimal gramsPerUnit;

        WeightUnit(BigDecimal gramsPerUnit) {
            this.gramsPerUnit = gramsPerUnit;
        }

        /**
         * Converts a value in this unit to grams.
         *
         * @param value The value in the current unit.
         * @return The value in grams.
         */
        public BigDecimal toGrams(BigDecimal value) {
            Objects.requireNonNull(value, "Value must not be null");
            if (value.signum() < 0) {
                throw new IllegalArgumentException("Value must not be negative");
            }
            return value.multiply(gramsPerUnit);
        }

        /**
         * Converts a value in grams to this unit.
         *
         * @param grams The value in grams.
         * @return The value in the current unit, rounded to the defined SCALE.
         */
        public BigDecimal fromGrams(BigDecimal grams) {
            Objects.requireNonNull(grams, "Grams must not be null");
            if (grams.signum() < 0) {
                throw new IllegalArgumentException("Grams must not be negative");
            }
            return grams.divide(gramsPerUnit, SCALE, ROUNDING_MODE).stripTrailingZeros();
        }
    }
}

