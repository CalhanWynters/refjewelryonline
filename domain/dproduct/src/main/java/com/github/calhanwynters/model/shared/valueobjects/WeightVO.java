package com.github.calhanwynters.model.shared.valueobjects;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Domain value object representing a product weight.
 */
public record WeightVO(
        BigDecimal amount,
        WeightUnit unit
) implements Comparable<WeightVO> { // Implements Comparable
    private static final BigDecimal MAX_GRAMS = new BigDecimal("100000.0");
    private static final int SCALE = 4;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    public WeightVO {
        Objects.requireNonNull(amount, "amount must not be null");
        Objects.requireNonNull(unit, "unit must not be null");
        if (amount.signum() < 0) {
            throw new IllegalArgumentException("amount must not be negative");
        }
        amount = amount.setScale(SCALE, ROUNDING_MODE).stripTrailingZeros();
        if (unit.toGrams(amount).compareTo(MAX_GRAMS) > 0) {
            throw new IllegalArgumentException("amount exceeds maximum allowed weight");
        }
    }

    // Factories
    public static WeightVO ofGrams(BigDecimal grams) { return new WeightVO(grams, WeightUnit.GRAM); }
    public static WeightVO ofOunces(BigDecimal ounces) { return new WeightVO(ounces, WeightUnit.OUNCE); }

    // Accessors / conversions
    public BigDecimal inGrams() {
        return unit.toGrams(amount).setScale(SCALE, ROUNDING_MODE).stripTrailingZeros();
    }
    public BigDecimal inOunces() {
        return unit.fromGrams(unit.toGrams(amount)).setScale(SCALE, ROUNDING_MODE).stripTrailingZeros(); // Re-derive using fromGrams
    }

    // Domain operations (immutably return new instances in canonical GRAM unit)
    public WeightVO add(WeightVO other) {
        Objects.requireNonNull(other);
        BigDecimal totalGrams = this.inGrams().add(other.inGrams());
        return WeightVO.ofGrams(totalGrams);
    }
    public WeightVO subtract(WeightVO other) {
        Objects.requireNonNull(other);
        BigDecimal resultGrams = this.inGrams().subtract(other.inGrams());
        if (resultGrams.signum() < 0) {
            throw new IllegalArgumentException("resulting weight must not be negative");
        }
        return WeightVO.ofGrams(resultGrams);
    }

    // Conversion utility
    public WeightVO toUnit(WeightUnit targetUnit) {
        if (this.unit.equals(targetUnit)) return this;
        BigDecimal resultInTargetUnit = targetUnit.fromGrams(this.inGrams());
        return new WeightVO(resultInTargetUnit, targetUnit);
    }

    // Standard comparison
    @Override
    public int compareTo(WeightVO other) {
        return this.inGrams().compareTo(other.inGrams());
    }

    // Enum for units using BigDecimal
    public enum WeightUnit {
        GRAM(BigDecimal.ONE),
        OUNCE(new BigDecimal("28.349523125")); // grams per unit

        private final BigDecimal gramsPerUnit;

        WeightUnit(BigDecimal gramsPerUnit) {
            this.gramsPerUnit = gramsPerUnit;
        }

        public BigDecimal toGrams(BigDecimal value) {
            return value.multiply(gramsPerUnit);
        }

        public BigDecimal fromGrams(BigDecimal grams) {
            return grams.divide(gramsPerUnit, SCALE, ROUNDING_MODE);
        }
    }
}
