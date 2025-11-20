package com.github.calhanwynters.model.shared.entities;

import com.github.calhanwynters.model.earringattributes.EarringSizeVO;
import com.github.calhanwynters.model.earringattributes.EarringStyleVO;
import com.github.calhanwynters.model.shared.valueobjects.CareInstructionVO;
import com.github.calhanwynters.model.shared.valueobjects.GemstoneVO;
import com.github.calhanwynters.model.shared.valueobjects.MaterialCompositionVO;
import com.github.calhanwynters.model.shared.valueobjects.PriceVO;
import com.github.calhanwynters.model.shared.valueobjects.VariantId;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public record EarringVariant(
        VariantId id,
        EarringSizeVO size,
        EarringStyleVO style,
        PriceVO price,
        Set<MaterialCompositionVO> materials,
        Set<GemstoneVO> gemstones,
        CareInstructionVO careInstructions
) implements Variant {

    public EarringVariant {
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

    public static EarringVariant create(
            EarringSizeVO size,
            EarringStyleVO style,
            PriceVO price,
            Set<MaterialCompositionVO> materials,
            CareInstructionVO careInstructions
    ) {
        // We use Set.of() for an empty immutable set of gemstones by default
        return new EarringVariant(VariantId.generate(), size, style, price, materials, Set.of(), careInstructions);
    }

    // --- Behavior Methods ---

    public EarringVariant changePrice(PriceVO newPrice) {
        return new EarringVariant(this.id, this.size, this.style, newPrice, this.materials, this.gemstones, this.careInstructions);
    }

    public EarringVariant addGemstone(GemstoneVO gemstone) {
        Set<GemstoneVO> newGemstones = new HashSet<>(this.gemstones);
        newGemstones.add(gemstone);
        return new EarringVariant(this.id, this.size, this.style, this.price, this.materials, newGemstones, this.careInstructions);
    }

    /**
     * Removes a gemstone from the variant.
     * @param gemstone The gemstone to remove.
     * @return A new EarringVariant instance without the specified gemstone.
     */
    public EarringVariant removeGemstone(GemstoneVO gemstone) {
        Set<GemstoneVO> newGemstones = new HashSet<>(this.gemstones);
        if (newGemstones.remove(gemstone)) {
            return new EarringVariant(this.id, this.size, this.style, this.price, this.materials, newGemstones, this.careInstructions);
        }
        return this; // Return the current instance if no change occurred
    }

    /**
     * Adds a material composition.
     */
    public EarringVariant addMaterial(MaterialCompositionVO material) {
        Set<MaterialCompositionVO> newMaterials = new HashSet<>(this.materials);
        newMaterials.add(material);
        return new EarringVariant(this.id, this.size, this.style, this.price, newMaterials, this.gemstones, this.careInstructions);
    }

    /**
     * Removes a material composition.
     * @param material The material to remove.
     * @return A new EarringVariant instance without the specified material.
     */
    public EarringVariant removeMaterial(MaterialCompositionVO material) {
        Set<MaterialCompositionVO> newMaterials = new HashSet<>(this.materials);
        if (newMaterials.remove(material)) {
            return new EarringVariant(this.id, this.size, this.style, this.price, newMaterials, this.gemstones, this.careInstructions);
        }
        return this; // Return the current instance if no change occurred
    }

    /**
     * Updates the care instructions.
     */
    public EarringVariant changeCareInstructions(CareInstructionVO newInstructions) {
        return new EarringVariant(this.id, this.size, this.style, this.price, this.materials, this.gemstones, newInstructions);
    }
}
