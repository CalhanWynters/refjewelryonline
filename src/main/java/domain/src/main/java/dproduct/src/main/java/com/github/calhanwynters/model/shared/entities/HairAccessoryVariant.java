package com.github.calhanwynters.model.shared.entities;

import com.github.calhanwynters.model.hairaccessoryattributes.HairAccessorStyleVO;
import com.github.calhanwynters.model.hairaccessoryattributes.HairAccessorySizeVO;
import com.github.calhanwynters.model.shared.valueobjects.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.Collections;

public record HairAccessoryVariant(
        VariantId id, // Using the shared VariantId from valueobjects
        HairAccessorySizeVO size, // Specific VO
        HairAccessorStyleVO style, // Specific VO
        PriceVO price,
        Set<MaterialCompositionVO> materials,
        Set<GemstoneVO> gemstones,
        CareInstructionVO careInstructions
) implements Variant { // Implements the shared Variant interface

    public HairAccessoryVariant {
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

    public static HairAccessoryVariant create(
            HairAccessorySizeVO size,
            HairAccessorStyleVO style,
            PriceVO price,
            Set<MaterialCompositionVO> materials,
            CareInstructionVO careInstructions
    ) {
        // We use the shared VariantId.generate() here
        return new HairAccessoryVariant(VariantId.generate(), size, style, price, materials, Set.of(), careInstructions);
    }

    // --- Behavior Methods ---

    public HairAccessoryVariant changePrice(PriceVO newPrice) {
        return new HairAccessoryVariant(this.id, this.size, this.style, newPrice, this.materials, this.gemstones, this.careInstructions);
    }

    public HairAccessoryVariant addGemstone(GemstoneVO gemstone) {
        Set<GemstoneVO> newGemstones = new HashSet<>(this.gemstones);
        newGemstones.add(gemstone);
        return new HairAccessoryVariant(this.id, this.size, this.style, this.price, this.materials, newGemstones, this.careInstructions);
    }

    /**
     * Removes a gemstone from the variant.
     * @param gemstone The gemstone to remove.
     * @return A new HairAccessoryVariant instance without the specified gemstone.
     */
    public HairAccessoryVariant removeGemstone(GemstoneVO gemstone) {
        Set<GemstoneVO> newGemstones = new HashSet<>(this.gemstones);
        if (newGemstones.remove(gemstone)) {
            return new HairAccessoryVariant(this.id, this.size, this.style, this.price, this.materials, newGemstones, this.careInstructions);
        }
        return this; // Return the current instance if no change occurred
    }

    /**
     * Adds a material composition.
     */
    public HairAccessoryVariant addMaterial(MaterialCompositionVO material) {
        Set<MaterialCompositionVO> newMaterials = new HashSet<>(this.materials);
        newMaterials.add(material);
        return new HairAccessoryVariant(this.id, this.size, this.style, this.price, newMaterials, this.gemstones, this.careInstructions);
    }

    /**
     * Removes a material composition.
     * @param material The material to remove.
     * @return A new HairAccessoryVariant instance without the specified material.
     */
    public HairAccessoryVariant removeMaterial(MaterialCompositionVO material) {
        Set<MaterialCompositionVO> newMaterials = new HashSet<>(this.materials);
        if (newMaterials.remove(material)) {
            return new HairAccessoryVariant(this.id, this.size, this.style, this.price, newMaterials, this.gemstones, this.careInstructions);
        }
        return this; // Return the current instance if no change occurred
    }

    /**
     * Updates the care instructions.
     */
    public HairAccessoryVariant changeCareInstructions(CareInstructionVO newInstructions) {
        return new HairAccessoryVariant(this.id, this.size, this.style, this.price, this.materials, this.gemstones, newInstructions);
    }
}
