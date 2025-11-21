package com.github.calhanwynters.model.shared.valueobjects;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Domain value object representing a product weight.
 * - Immutable record, domain-only (no infra annotations).
 * - Stores amount as BigDecimal and unit as WeightUnit.
 * - Validation enforces non-negative and sensible upper bounds.
 * - Provides conversions and value-based operations returning new instances.
 */
public record WeightVO(
        BigDecimal amount,   // stored value in provided unit (non-null, non-negative)
        WeightUnit unit      // canonical unit enum (non-null)
) {
    private static final BigDecimal MAX_GRAMS = new BigDecimal("100000.0"); // 100 kg upper bound
    private static final int SCALE = 4; // Precision for weight calculations

    // Compact constructor with validation and normalization
    public WeightVO {
        Objects.requireNonNull(amount, "amount must not be null");
        Objects.requireNonNull(unit, "unit must not be null");

        if (amount.signum() < 0) {
            throw new IllegalArgumentException("amount must not be negative");
        }

        // Enforce scale and rounding for internal consistency
        amount = amount.setScale(SCALE, RoundingMode.HALF_UP).stripTrailingZeros();

        if (unit.toGrams(amount).compareTo(MAX_GRAMS) > 0) {
            throw new IllegalArgumentException("amount exceeds maximum allowed weight");
        }
    }

    // Factories
    public static WeightVO ofGrams(BigDecimal grams) {
        return new WeightVO(grams, WeightUnit.GRAM);
    }

    public static WeightVO ofOunces(BigDecimal ounces) {
        return new WeightVO(ounces, WeightUnit.OUNCE);
    }

    // Accessors / conversions
    public BigDecimal inGrams() {
        return unit.toGrams(amount).setScale(SCALE, RoundingMode.HALF_UP).stripTrailingZeros();
    }

    public BigDecimal inOunces() {
        return unit.toOunces(amount).setScale(SCALE, RoundingMode.HALF_UP).stripTrailingZeros();
    }

    // Domain operations (immutably return new instances)
    public WeightVO add(WeightVO other) {
        Objects.requireNonNull(other);
        BigDecimal totalGrams = this.inGrams().add(other.inGrams());
        // Return a new VO using the original unit of this object
        BigDecimal resultInUnit = unit.fromGrams(totalGrams);
        return new WeightVO(resultInUnit, unit);
    }

    public WeightVO subtract(WeightVO other) {
        Objects.requireNonNull(other);
        BigDecimal resultGrams = this.inGrams().subtract(other.inGrams());
        if (resultGrams.signum() < 0) {
            throw new IllegalArgumentException("resulting weight must not be negative");
        }
        // Return a new VO using the original unit of this object
        BigDecimal resultInUnit = unit.fromGrams(resultGrams);
        return new WeightVO(resultInUnit, unit);
    }

    public int compareByGrams(WeightVO other) {
        return this.inGrams().compareTo(other.inGrams());
    }

    // Enum for units using BigDecimal
    public enum WeightUnit {
        GRAM {
            @Override public BigDecimal toGrams(BigDecimal v) { return v; }
            @Override public BigDecimal fromGrams(BigDecimal g) { return g; }
            @Override public BigDecimal toOunces(BigDecimal v) { return v.divide(OUNCES_PER_GRAM, SCALE, RoundingMode.HALF_UP); }
        },
        OUNCE {
            @Override public BigDecimal toGrams(BigDecimal v) { return v.multiply(GRAMS_PER_OUNCE); }
            @Override public BigDecimal fromGrams(BigDecimal g) { return g.divide(GRAMS_PER_OUNCE, SCALE, RoundingMode.HALF_UP); }
            @Override public BigDecimal toOunces(BigDecimal v) { return v; }
        };

        private static final BigDecimal GRAMS_PER_OUNCE = new BigDecimal("28.349523125");
        private static final BigDecimal OUNCES_PER_GRAM = new BigDecimal("0.03527396195"); // 1/28.349...

        public abstract BigDecimal toGrams(BigDecimal value);
        public abstract BigDecimal fromGrams(BigDecimal grams);
        public abstract BigDecimal toOunces(BigDecimal value);
    }
}
