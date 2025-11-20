package com.github.calhanwynters.model.shared.entities;

import com.github.calhanwynters.model.necklaceattributes.NecklaceSizeVO;
import com.github.calhanwynters.model.necklaceattributes.NecklaceStyleVO;
import com.github.calhanwynters.model.shared.valueobjects.CareInstructionVO;
import com.github.calhanwynters.model.shared.valueobjects.GemstoneVO;
import com.github.calhanwynters.model.shared.valueobjects.MaterialCompositionVO;
import com.github.calhanwynters.model.shared.valueobjects.PriceVO;
import com.github.calhanwynters.model.shared.valueobjects.VariantId;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public record NecklaceVariant(
        VariantId id, // Using the shared VariantId from shared.valueobjects
        NecklaceSizeVO size, // Specific VO
        NecklaceStyleVO style, // Specific VO
        PriceVO price,
        Set<MaterialCompositionVO> materials,
        Set<GemstoneVO> gemstones,
        CareInstructionVO careInstructions
) implements Variant { // Implements the shared Variant interface

    public NecklaceVariant {
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

    public static NecklaceVariant create(
            NecklaceSizeVO size,
            NecklaceStyleVO style,
            PriceVO price,
            Set<MaterialCompositionVO> materials,
            CareInstructionVO careInstructions
    ) {
        // We use the shared VariantId.generate() here
        return new NecklaceVariant(VariantId.generate(), size, style, price, materials, Set.of(), careInstructions);
    }

    // --- Behavior Methods ---

    public NecklaceVariant changePrice(PriceVO newPrice) {
        return new NecklaceVariant(this.id, this.size, this.style, newPrice, this.materials, this.gemstones, this.careInstructions);
    }

    public NecklaceVariant addGemstone(GemstoneVO gemstone) {
        Set<GemstoneVO> newGemstones = new HashSet<>(this.gemstones);
        newGemstones.add(gemstone);
        return new NecklaceVariant(this.id, this.size, this.style, this.price, this.materials, newGemstones, this.careInstructions);
    }

    /**
     * Removes a gemstone from the variant.
     * @param gemstone The gemstone to remove.
     * @return A new NecklaceVariant instance without the specified gemstone.
     */
    public NecklaceVariant removeGemstone(GemstoneVO gemstone) {
        Set<GemstoneVO> newGemstones = new HashSet<>(this.gemstones);
        if (newGemstones.remove(gemstone)) {
            return new NecklaceVariant(this.id, this.size, this.style, this.price, this.materials, newGemstones, this.careInstructions);
        }
        return this; // Return the current instance if no change occurred
    }

    /**
     * Adds a material composition.
     */
    public NecklaceVariant addMaterial(MaterialCompositionVO material) {
        Set<MaterialCompositionVO> newMaterials = new HashSet<>(this.materials);
        newMaterials.add(material);
        return new NecklaceVariant(this.id, this.size, this.style, this.price, newMaterials, this.gemstones, this.careInstructions);
    }

    /**
     * Removes a material composition.
     * @param material The material to remove.
     * @return A new NecklaceVariant instance without the specified material.
     */
    public NecklaceVariant removeMaterial(MaterialCompositionVO material) {
        Set<MaterialCompositionVO> newMaterials = new HashSet<>(this.materials);
        if (newMaterials.remove(material)) {
            return new NecklaceVariant(this.id, this.size, this.style, this.price, newMaterials, this.gemstones, this.careInstructions);
        }
        return this; // Return the current instance if no change occurred
    }

    /**
     * Updates the care instructions.
     */
    public NecklaceVariant changeCareInstructions(CareInstructionVO newInstructions) {
        return new NecklaceVariant(this.id, this.size, this.style, this.price, this.materials, this.gemstones, newInstructions);
    }
}
