package com.github.calhanwynters.model.shared.valueobjects;

import java.math.BigDecimal;
import java.math.RoundingMode;

public enum WeightUnitVO {
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
    OUNCE {
        @Override
        public BigDecimal toGrams(BigDecimal v) {
            return v.multiply(GRAMS_PER_OUNCE);
        }

        @Override
        public BigDecimal fromGrams(BigDecimal g) {
            return g.divide(GRAMS_PER_OUNCE, SCALE, RoundingMode.HALF_UP);
        }
    };

    public static final BigDecimal GRAMS_PER_OUNCE = new BigDecimal("28.349523125");
    private static final int SCALE = 4;

    public abstract BigDecimal toGrams(BigDecimal value);
    public abstract BigDecimal fromGrams(BigDecimal grams);

    public BigDecimal convertTo(BigDecimal value, WeightUnitVO targetUnit) {
        if (this == targetUnit) {
            return value;
        }
        BigDecimal grams = this.toGrams(value);
        return targetUnit.fromGrams(grams);
    }
}
