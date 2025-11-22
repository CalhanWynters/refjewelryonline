package com.github.calhanwynters.model.shared.valueobjects;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.Currency;

/**
 * Domain value object representing a monetary price.
 * - Immutable record, domain-only (no infra annotations/deps).
 * - Stores amount as BigDecimal and Currency unit.
 * - Validation enforces non-negative amount.
 * - Provides conversions and value-based operations returning new instances.
 */
public record PriceVO(
        BigDecimal amount,
        Currency currency
) {
    // Define a standard scale for monetary operations (e.g., 2 decimal places for USD)
    private static final int CURRENCY_SCALE = 2;

    // Compact constructor with validation and normalization
    public PriceVO {
        Objects.requireNonNull(amount, "amount must not be null");
        Objects.requireNonNull(currency, "currency must not be null");

        if (amount.signum() < 0) {
            throw new IllegalArgumentException("amount must not be negative");
        }

        // Normalize the amount to the standard scale for consistency
        amount = amount.setScale(CURRENCY_SCALE, RoundingMode.HALF_UP);
    }

    // Factories
    /**
     * Creates a PriceVO in USD with the specified amount.
     * @param amount The monetary value.
     * @return A new PriceVO instance.
     */
    public static PriceVO ofUSD(BigDecimal amount) {
        return new PriceVO(amount, Currency.getInstance("USD"));
    }

    /**
     * Creates a PriceVO in the specified currency with the specified amount.
     * @param amount The monetary value.
     * @param currencyCode The ISO 4217 currency code (e.g., "EUR", "JPY").
     * @return A new PriceVO instance.
     */
    public static PriceVO of(BigDecimal amount, String currencyCode) {
        return new PriceVO(amount, Currency.getInstance(currencyCode));
    }

    // Domain operations (immutably return new instances)

    /**
     * Adds another price to this one, ensuring currencies match.
     * @param other The other PriceVO to add.
     * @return A new PriceVO with the summed amount.
     */
    public PriceVO add(PriceVO other) {
        Objects.requireNonNull(other);
        ensureSameCurrency(other);
        BigDecimal totalAmount = this.amount.add(other.amount);
        return new PriceVO(totalAmount, this.currency);
    }

    /**
     * Subtracts another price from this one, ensuring currencies match.
     * The resulting amount cannot be negative.
     * @param other The other PriceVO to subtract.
     * @return A new PriceVO with the difference.
     * @throws IllegalArgumentException if the result would be negative.
     */
    public PriceVO subtract(PriceVO other) {
        Objects.requireNonNull(other);
        ensureSameCurrency(other);
        BigDecimal resultAmount = this.amount.subtract(other.amount);
        if (resultAmount.signum() < 0) {
            throw new IllegalArgumentException("resulting price must not be negative");
        }
        return new PriceVO(resultAmount, this.currency);
    }

    /**
     * Multiplies the price by a factor.
     * @param factor The multiplication factor.
     * @return A new PriceVO with the multiplied amount.
     */
    public PriceVO multiply(BigDecimal factor) {
        BigDecimal resultAmount = this.amount.multiply(factor);
        return new PriceVO(resultAmount, this.currency);
    }

    /**
     * Helper method to ensure currency compatibility during operations.
     */
    private void ensureSameCurrency(PriceVO other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot operate on prices with different currencies: "
                    + this.currency.getCurrencyCode() + " vs " + other.currency.getCurrencyCode());
        }
    }

    // Comparison based on value (amount and currency)
    public int compareTo(PriceVO other) {
        ensureSameCurrency(other);
        return this.amount.compareTo(other.amount);
    }

    // Display formatting (example, could be enhanced with a proper formatter)
    public String formattedDisplay() {
        // Use a standard number formatter for production code
        return this.currency.getSymbol() + " " + this.amount.toPlainString();
    }
}
