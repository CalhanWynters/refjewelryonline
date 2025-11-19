package com.github.calhanwynters.model.shared.valueobjects;

import java.util.Objects;
import java.util.UUID;

/**
 * Domain value object representing a product weight.
 * - Immutable record, domain-only (no infra annotations).
 * - Stores amount as double and unit as WeightUnit.
 * - Factories validate invariants (non-negative, sensible upper bound).
 * - Provides conversions and value-based operations returning new instances.
 */
public record WeightVO(
        String id,
        double amount,       // stored value in provided unit
        WeightUnit unit      // canonical unit enum
) {
    private static final double MAX_GRAMS = 100_000.0; // sensible upper bound (100 kg)
    private static final double EPSILON = 1e-9;

    // Factories
    public static WeightVO ofGrams(double grams) {
        return create(UUID.randomUUID().toString(), grams, WeightUnit.GRAM);
    }

    public static WeightVO ofOunces(double ounces) {
        return create(UUID.randomUUID().toString(), ounces, WeightUnit.OUNCE);
    }

    public static WeightVO withId(String id, double amount, WeightUnit unit) {
        return create(Objects.requireNonNull(id), amount, Objects.requireNonNull(unit));
    }

    public static WeightVO create(String id, double amount, WeightUnit unit) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(unit, "unit must not be null");
        if (Double.isNaN(amount) || Double.isInfinite(amount)) throw new IllegalArgumentException("amount must be a finite number");
        if (amount < 0.0 - EPSILON) throw new IllegalArgumentException("amount must not be negative");
        double grams = unit.toGrams(amount);
        if (grams > MAX_GRAMS + EPSILON) throw new IllegalArgumentException("amount exceeds maximum allowed weight");
        return new WeightVO(id, amount, unit);
    }

    // Accessors / conversions
    public double inGrams() {
        return unit.toGrams(amount);
    }

    public double inOunces() {
        return unit.toOunces(amount);
    }

    // Domain operations (immutably return new instances)
    public WeightVO add(WeightVO other) {
        Objects.requireNonNull(other);
        double totalGrams = this.inGrams() + other.inGrams();
        double resultInUnit = unit.fromGrams(totalGrams);
        return create(UUID.randomUUID().toString(), resultInUnit, unit);
    }

    public WeightVO subtract(WeightVO other) {
        Objects.requireNonNull(other);
        double resultGrams = this.inGrams() - other.inGrams();
        if (resultGrams < -EPSILON) throw new IllegalArgumentException("resulting weight must not be negative");
        double resultInUnit = unit.fromGrams(Math.max(0.0, resultGrams));
        return create(UUID.randomUUID().toString(), resultInUnit, unit);
    }

    public int compareByGrams(WeightVO other) {
        return Double.compare(this.inGrams(), other.inGrams());
    }

    public boolean sameValue(WeightVO other) {
        if (other == null) return false;
        return Math.abs(this.inGrams() - other.inGrams()) < 1e-6;
    }

    // Helpers / overrides
    @Override
    public String toString() {
        return "WeightVO[id=" + id + ", amount=" + amount + ", unit=" + unit + "]";
    }

    public enum WeightUnit {
        GRAM {
            @Override public double toGrams(double v) { return v; }
            @Override public double fromGrams(double g) { return g; }
            @Override public double toOunces(double v) { return v / 28.349523125; }
        },
        OUNCE {
            @Override public double toGrams(double v) { return v * 28.349523125; }
            @Override public double fromGrams(double g) { return g / 28.349523125; }
            @Override public double toOunces(double v) { return v; }
        };

        public abstract double toGrams(double value);
        public abstract double fromGrams(double grams);
        public abstract double toOunces(double value);
    }
}
