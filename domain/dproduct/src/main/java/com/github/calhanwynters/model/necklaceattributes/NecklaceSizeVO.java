package com.github.calhanwynters.model.necklaceattributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.Optional;

/**
 * Domain value object representing a standardized necklace size, using length in inches as the canonical representation.
 * - Immutable record, domain-only (no infra annotations/deps).
 * - Stores length in inches (BigDecimal for precision).
 * - Provides conversions to common units (cm, inches).
 * - Encapsulates standard necklace lengths (e.g., choker, princess).
 */
public record NecklaceSizeVO(BigDecimal lengthInches)
        implements Comparable<NecklaceSizeVO> {

    private static final int LENGTH_SCALE = 2;
    private static final BigDecimal INCHES_PER_CM =
            new BigDecimal("0.3937007874");

    public NecklaceSizeVO {
        Objects.requireNonNull(lengthInches, "lengthInches must not be null");
        if (lengthInches.signum() <= 0)
            throw new IllegalArgumentException("lengthInches must be positive");

        lengthInches = lengthInches
                .setScale(LENGTH_SCALE, RoundingMode.HALF_UP); // do not strip zeros
    }

    public static NecklaceSizeVO ofInches(BigDecimal inches) {
        return new NecklaceSizeVO(inches);
    }

    public static NecklaceSizeVO ofCentimeters(BigDecimal cm) {
        BigDecimal inches = cm.multiply(INCHES_PER_CM)
                .setScale(LENGTH_SCALE, RoundingMode.HALF_UP);
        return new NecklaceSizeVO(inches);
    }

    public BigDecimal inInches() { return lengthInches; }

    public BigDecimal inCentimeters() {
        return lengthInches
                .divide(INCHES_PER_CM, LENGTH_SCALE, RoundingMode.HALF_UP);
    }

    public Optional<String> getStandardLengthName() {
        BigDecimal inches = lengthInches;

        if (inches.compareTo(BigDecimal.valueOf(14)) <= 0) return Optional.of("Collar");
        if (inches.compareTo(BigDecimal.valueOf(16)) <= 0) return Optional.of("Choker");
        if (inches.compareTo(BigDecimal.valueOf(18)) <= 0) return Optional.of("Princess");
        if (inches.compareTo(BigDecimal.valueOf(20)) <= 0) return Optional.of("Matinee");
        if (inches.compareTo(BigDecimal.valueOf(34)) <= 0) return Optional.of("Opera");
        return Optional.of("Rope/Lariat");
    }

    @Override
    public int compareTo(NecklaceSizeVO other) {
        return this.lengthInches.compareTo(other.lengthInches);
    }
}
