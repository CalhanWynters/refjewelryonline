package com.github.calhanwynters.model.hairaccessory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.Optional;

public record HairAccessorySizeVO(
        BigDecimal lengthMm, // Primary dimension
        BigDecimal widthMm,  // Secondary dimension (optional)
        String descriptionLabel // e.g., "Medium", "Large"
) {
    private static final int SCALE = 2;

    public HairAccessorySizeVO {
        Objects.requireNonNull(lengthMm, "lengthMm must not be null");
        if (lengthMm.signum() <= 0) throw new IllegalArgumentException("Length must be positive");

        lengthMm = lengthMm.setScale(SCALE, RoundingMode.HALF_UP).stripTrailingZeros();
        widthMm = (widthMm == null) ? null : widthMm.setScale(SCALE, RoundingMode.HALF_UP).stripTrailingZeros();
    }

    public static HairAccessorySizeVO ofLength(BigDecimal mm, String label) {
        return new HairAccessorySizeVO(mm, null, label);
    }

    public Optional<BigDecimal> getWidthMm() {
        return Optional.ofNullable(widthMm);
    }

    public Optional<String> getDescriptionLabel() {
        return Optional.ofNullable(descriptionLabel);
    }
}
