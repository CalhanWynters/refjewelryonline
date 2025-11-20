package com.github.calhanwynters.model.shared.valueobjects;

import java.util.Objects;

/**
 * Domain value object representing a material and its role within a product.
 * Examples: (MaterialVO("Gold"), "band"), (MaterialVO("Platinum"), "prongs").
 */
public record MaterialCompositionVO(
        MaterialVO material,
        String role
) {
    // Compact constructor with validation
    public MaterialCompositionVO {
        Objects.requireNonNull(material, "material must not be null");
        Objects.requireNonNull(role, "role must not be null");
        if (role.isBlank()) {
            throw new IllegalArgumentException("role cannot be empty or blank");
        }
    }
}
