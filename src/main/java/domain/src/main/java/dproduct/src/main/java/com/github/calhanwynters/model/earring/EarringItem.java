package com.github.calhanwynters.model.earring;

import com.github.calhanwynters.model.shared.valueobjects.CareInstructionVO;
import com.github.calhanwynters.model.shared.valueobjects.DescriptionVO;
import com.github.calhanwynters.model.shared.valueobjects.GemstoneVO;
import com.github.calhanwynters.model.shared.valueobjects.MaterialCompositionVO;
import com.github.calhanwynters.model.shared.valueobjects.PriceVO;

import java.util.*;

/**
 * Aggregate Root representing an Earring item in the domain.
 * An immutable record that controls access to its internal components
 * and enforces business invariants.
 */
public record EarringItem(
        EarringId id,
        EarringSizeVO size,
        EarringStyleVO style,
        PriceVO price,
        Set<MaterialCompositionVO> materials,
        Set<GemstoneVO> gemstones,
        DescriptionVO description,
        CareInstructionVO careInstructions
) {
    // Compact constructor to ensure all fields are non-null and collections are immutable
    public EarringItem {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(size, "size must not be null");
        Objects.requireNonNull(style, "style must not be null");
        Objects.requireNonNull(price, "price must not be null");
        Objects.requireNonNull(materials, "materials must not be null");
        Objects.requireNonNull(description, "description must not be null");
        Objects.requireNonNull(gemstones, "gemstones must not be null");
        Objects.requireNonNull(careInstructions, "careInstructions must not be null"); // Check the new field

        // Ensure the internal sets are unmodifiable copies
        materials = Set.copyOf(materials);
        gemstones = Set.copyOf(gemstones);
    }

    // Factory method for creating a new earring from required VOs
    public static EarringItem create(
            EarringSizeVO size,
            EarringStyleVO style,
            PriceVO price,
            Set<MaterialCompositionVO> materials,
            DescriptionVO description,
            CareInstructionVO careInstructions // Added parameter
    ) {
        return new EarringItem(EarringId.generate(), size, style, price, materials, Collections.emptySet(), description, careInstructions);
    }

    // --- Domain Behaviors (returning new instances for immutability) ---

    public EarringItem changePrice(PriceVO newPrice) {
        return new EarringItem(this.id, this.size, this.style, newPrice, this.materials, this.gemstones, this.description, this.careInstructions);
    }

    public EarringItem changeDescription(DescriptionVO newDescription) {
        return new EarringItem(this.id, this.size, this.style, this.price, this.materials, this.gemstones, newDescription, this.careInstructions);
    }

    public EarringItem addGemstone(GemstoneVO gemstone) {
        Set<GemstoneVO> newGemstones = new HashSet<>(this.gemstones);
        newGemstones.add(gemstone);
        return new EarringItem(this.id, this.size, this.style, this.price, this.materials, newGemstones, this.description, this.careInstructions);
    }

    /**
     * Adds a material composition to the earring.
     * @param material The material composition value object to add.
     * @return A new EarringItem instance with the new material.
     */
    public EarringItem addMaterial(MaterialCompositionVO material) {
        Set<MaterialCompositionVO> newMaterials = new HashSet<>(this.materials);
        newMaterials.add(material);
        return new EarringItem(this.id, this.size, this.style, this.price, newMaterials, this.gemstones, this.description, this.careInstructions);
    }

    /**
     * Updates the care instructions (e.g., after expert consultation).
     * @param newInstructions The updated care instructions.
     * @return A new EarringItem instance with updated instructions.
     */
    public EarringItem changeCareInstructions(CareInstructionVO newInstructions) {
        return new EarringItem(this.id, this.size, this.style, this.price, this.materials, this.gemstones, this.description, newInstructions);
    }
}
