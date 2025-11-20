package com.github.calhanwynters.model.ring;

import com.github.calhanwynters.model.shared.valueobjects.CareInstructionVO;
import com.github.calhanwynters.model.shared.valueobjects.DescriptionVO;
import com.github.calhanwynters.model.shared.valueobjects.GemstoneVO;
import com.github.calhanwynters.model.shared.valueobjects.MaterialCompositionVO;
import com.github.calhanwynters.model.shared.valueobjects.PriceVO;

import java.util.*;

/**
 * Aggregate Root representing a Ring item in the domain.
 * An immutable record that controls access to its internal components
 * and enforces business invariants.
 */
public record RingItem(
        RingId id,
        RingSizeVO size,
        RingStyleVO style,
        PriceVO price,
        Set<MaterialCompositionVO> materials,
        Set<GemstoneVO> gemstones,
        DescriptionVO description,
        CareInstructionVO careInstructions
) {
    public RingItem {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(size, "size must not be null");
        Objects.requireNonNull(style, "style must not be null");
        Objects.requireNonNull(price, "price must not be null");
        Objects.requireNonNull(materials, "materials must not be null");
        Objects.requireNonNull(description, "description must not be null");
        Objects.requireNonNull(gemstones, "gemstones must not be null");
        Objects.requireNonNull(careInstructions, "careInstructions must not be null"); // Check the new field

        materials = Set.copyOf(materials);
        gemstones = Set.copyOf(gemstones);
    }

    public static RingItem create(
            RingSizeVO size,
            RingStyleVO style,
            PriceVO price,
            Set<MaterialCompositionVO> materials,
            DescriptionVO description,
            CareInstructionVO careInstructions // Added parameter
    ) {
        return new RingItem(RingId.generate(), size, style, price, materials, Collections.emptySet(), description, careInstructions);
    }

    // --- Domain Behaviors (returning new instances for immutability) ---

    public RingItem changePrice(PriceVO newPrice) {
        return new RingItem(this.id, this.size, this.style, newPrice, this.materials, this.gemstones, this.description, this.careInstructions);
    }

    public RingItem changeDescription(DescriptionVO newDescription) {
        return new RingItem(this.id, this.size, this.style, this.price, this.materials, this.gemstones, newDescription, this.careInstructions);
    }

    public RingItem addGemstone(GemstoneVO gemstone) {
        Set<GemstoneVO> newGemstones = new HashSet<>(this.gemstones);
        newGemstones.add(gemstone);
        return new RingItem(this.id, this.size, this.style, this.price, this.materials, newGemstones, this.description, this.careInstructions);
    }

    public RingItem addMaterial(MaterialCompositionVO newMaterial) {
        Set<MaterialCompositionVO> newMaterials = new HashSet<>(this.materials);
        newMaterials.add(newMaterial);
        return new RingItem(this.id, this.size, this.style, this.price, newMaterials, this.gemstones, this.description, this.careInstructions);
    }

    public RingItem changeSize(RingSizeVO newSize) {
        return new RingItem(this.id, newSize, this.style, this.price, this.materials, this.gemstones, this.description, this.careInstructions);
    }

    /**
     * Updates the care instructions (e.g., after expert consultation).
     * @param newInstructions The updated care instructions.
     * @return A new RingItem instance with updated instructions.
     */
    public RingItem changeCareInstructions(CareInstructionVO newInstructions) {
        return new RingItem(this.id, this.size, this.style, this.price, this.materials, this.gemstones, this.description, newInstructions);
    }
}
