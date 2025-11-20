package com.github.calhanwynters.model.anklet;

import com.github.calhanwynters.model.shared.valueobjects.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public record AnkletVariant(
        AnkletVariantId id,
        AnkletSizeVO size,
        AnkletStyleVO style,
        PriceVO price,
        Set<MaterialCompositionVO> materials,
        Set<GemstoneVO> gemstones,
        CareInstructionVO careInstructions
) {
    public AnkletVariant {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(size, "size must not be null");
        Objects.requireNonNull(style, "style must not be null");
        Objects.requireNonNull(price, "price must not be null");
        Objects.requireNonNull(materials, "materials must not be null");
        Objects.requireNonNull(gemstones, "gemstones must not be null");
        Objects.requireNonNull(careInstructions, "careInstructions must not be null");

        // Use Set.copyOf() in the compact constructor for immutability
        materials = Set.copyOf(materials);
        gemstones = Set.copyOf(gemstones);
    }

    public static AnkletVariant create(
            AnkletSizeVO size,
            AnkletStyleVO style,
            PriceVO price,
            Set<MaterialCompositionVO> materials,
            CareInstructionVO careInstructions
    ) {
        // Use Set.of() for an empty immutable set
        return new AnkletVariant(AnkletVariantId.generate(), size, style, price, materials, Set.of(), careInstructions);
    }

    // --- Behavior Methods ---

    public AnkletVariant changePrice(PriceVO newPrice) {
        return new AnkletVariant(this.id, this.size, this.style, newPrice, this.materials, this.gemstones, this.careInstructions);
    }

    public AnkletVariant addGemstone(GemstoneVO gemstone) {
        Set<GemstoneVO> newGemstones = new HashSet<>(this.gemstones);
        newGemstones.add(gemstone);
        return new AnkletVariant(this.id, this.size, this.style, this.price, this.materials, newGemstones, this.careInstructions);
    }

    /**
     * Adds a material composition.
     */
    public AnkletVariant addMaterial(MaterialCompositionVO material) {
        Set<MaterialCompositionVO> newMaterials = new HashSet<>(this.materials);
        newMaterials.add(material);
        return new AnkletVariant(this.id, this.size, this.style, this.price, newMaterials, this.gemstones, this.careInstructions);
    }

    /**
     * Updates the care instructions.
     */
    public AnkletVariant changeCareInstructions(CareInstructionVO newInstructions) {
        return new AnkletVariant(this.id, this.size, this.style, this.price, this.materials, this.gemstones, newInstructions);
    }
}
