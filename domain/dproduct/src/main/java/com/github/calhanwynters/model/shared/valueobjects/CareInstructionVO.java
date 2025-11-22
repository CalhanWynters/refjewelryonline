package com.github.calhanwynters.model.shared.valueobjects;

import java.util.Objects;

/**
 * Domain value object representing the specific care instructions for a product.
 */
public record CareInstructionVO(String instructions) {
    public CareInstructionVO {
        Objects.requireNonNull(instructions, "Instructions cannot be null");
        if (instructions.isBlank()) {
            throw new IllegalArgumentException("Instructions cannot be empty or blank");
        }
    }

    public String display() {
        return instructions;
    }
}
