package com.github.calhanwynters.model.shared.valueobjects;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record PercentageVO(BigDecimal value) {
    public PercentageVO {
        Objects.requireNonNull(value, "value must not be null");
        if (value.compareTo(BigDecimal.ZERO) < 0 || value.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException("Percentage must be between 0 and 1 (0.0 for 0%, 1.0 for 100%).");
        }
        // Normalize the scale for consistency
        value = value.setScale(4, RoundingMode.HALF_UP);
    }
}
