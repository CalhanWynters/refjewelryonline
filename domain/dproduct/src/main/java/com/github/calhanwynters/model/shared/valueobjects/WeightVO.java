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

    // Compact constructor for validation and normalization
    public WeightVO {
        Objects.requireNonNull(amount, "amount must not be null");
        Objects.requireNonNull(unit, "unit must not be null");
        if (amount.signum() < 0) {
            throw new IllegalArgumentException("amount must not be negative");
        }
        // Normalize the input amount using the unit's defined scale and rounding
        amount = amount.setScale(WeightUnit.SCALE, WeightUnit.ROUNDING_MODE).stripTrailingZeros();

        // Validate total weight against maximum allowed
        if (unit.toGrams(amount).compareTo(MAX_GRAMS) > 0) {
            throw new IllegalArgumentException("amount exceeds maximum allowed weight");
        }
    }

    // Factories
    public static WeightVO ofGrams(BigDecimal grams) { return new WeightVO(grams, WeightUnit.GRAM); }
    public static WeightVO ofOunces(BigDecimal ounces) { return new WeightVO(ounces, WeightUnit.OUNCE); }
    public static WeightVO ofTroyOunces(BigDecimal troyOunces) { return new WeightVO(troyOunces, WeightUnit.TROY_OUNCE); }
    public static WeightVO ofCarats(BigDecimal carats) { return new WeightVO(carats, WeightUnit.CARAT); }


    // Accessors / conversions (use the unit's methods, applying consistent final scaling)
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

    // Domain operations (immutably return new instances in canonical GRAM unit for arithmetic)
    public WeightVO add(WeightVO other) {
        Objects.requireNonNull(other);
        BigDecimal totalGrams = this.inGrams().add(other.inGrams());
        // Return a new VO, which applies validation and normalization in its constructor
        return WeightVO.ofGrams(totalGrams);
    }

    public WeightVO subtract(WeightVO other) {
        Objects.requireNonNull(other);
        BigDecimal resultGrams = this.inGrams().subtract(other.inGrams());
        if (resultGrams.signum() < 0) {
            throw new IllegalArgumentException("resulting weight must not be negative");
        }
        // Return a new VO, which applies validation and normalization in its constructor
        return WeightVO.ofGrams(resultGrams);
    }

    // Standard comparison based on canonical unit (grams)
    @Override
    public int compareTo(WeightVO other) {
        return this.inGrams().compareTo(other.inGrams());
    }

    /**
     * Enum for supported weight units using BigDecimal for precision.
     */
    public enum WeightUnit {
        GRAM(BigDecimal.ONE),
        OUNCE(new BigDecimal("28.349523125")), // Avoirdupois Ounce (general goods)
        TROY_OUNCE(new BigDecimal("31.1034768")), // Troy Ounce (precious metals)
        CARAT(new BigDecimal("0.2")); // Metric Carat (gemstones)

        // Centralized precision constants for the entire WeightVO system
        public static final int SCALE = 4;
        public static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

        private final BigDecimal gramsPerUnit;

        WeightUnit(BigDecimal gramsPerUnit) {
            this.gramsPerUnit = gramsPerUnit;
        }

        /**
         * Converts a value in this unit to grams.
         * Note: Does not apply final rounding/scaling.
         */
        public BigDecimal toGrams(BigDecimal value) {
            return value.multiply(gramsPerUnit);
        }

        /**
         * Converts a value in grams to this unit, applying the defined SCALE and ROUNDING_MODE.
         */
        public BigDecimal fromGrams(BigDecimal grams) {
            return grams.divide(gramsPerUnit, SCALE, ROUNDING_MODE);
        }
    }
}
