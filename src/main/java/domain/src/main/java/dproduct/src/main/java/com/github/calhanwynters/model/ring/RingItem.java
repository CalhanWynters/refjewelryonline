package com.github.calhanwynters.model.ring;

import com.github.calhanwynters.model.shared.valueobjects.DescriptionVO;
import com.github.calhanwynters.model.shared.valueobjects.GemstoneVO;
import com.github.calhanwynters.model.shared.valueobjects.MaterialCompositionVO;
import com.github.calhanwynters.model.shared.valueobjects.PriceVO;

import java.util.*;

/**
 * Aggregate Root representing a Ring item in the domain.
 */
public record RingItem(
        RingId id,
        RingSizeVO size,
        RingStyleVO style,
        PriceVO price,
        Set<MaterialCompositionVO> materials, // Updated to use a Set of MaterialCompositionVO
        Set<GemstoneVO> gemstones,
        DescriptionVO description
) {
    public RingItem {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(size, "size must not be null");
        Objects.requireNonNull(style, "style must not be null");
        Objects.requireNonNull(price, "price must not be null");
        Objects.requireNonNull(materials, "materials must not be null"); // Check the new field
        Objects.requireNonNull(description, "description must not be null");
        Objects.requireNonNull(gemstones, "gemstones must not be null");

        materials = Set.copyOf(materials);
        gemstones = Set.copyOf(gemstones);
    }

    public static RingItem create(
            RingSizeVO size,
            RingStyleVO style,
            PriceVO price,
            Set<MaterialCompositionVO> materials, // Updated parameter
            DescriptionVO description
    ) {
        return new RingItem(RingId.generate(), size, style, price, materials, Collections.emptySet(), description);
    }

    // --- Domain Behaviors (returning new instances for immutability) ---

    public RingItem changePrice(PriceVO newPrice) {
        return new RingItem(this.id, this.size, this.style, newPrice, this.materials, this.gemstones, this.description);
    }

    public RingItem changeDescription(DescriptionVO newDescription) {
        return new RingItem(this.id, this.size, this.style, this.price, this.materials, this.gemstones, newDescription);
    }

    public RingItem addGemstone(GemstoneVO gemstone) {
        Set<GemstoneVO> newGemstones = new HashSet<>(this.gemstones);
        newGemstones.add(gemstone);
        return new RingItem(this.id, this.size, this.style, this.price, this.materials, newGemstones, this.description);
    }

    public RingItem addMaterial(MaterialCompositionVO newMaterial) {
        Set<MaterialCompositionVO> newMaterials = new HashSet<>(this.materials);
        newMaterials.add(newMaterial);
        return new RingItem(this.id, this.size, this.style, this.price, newMaterials, this.gemstones, this.description);
    }

    public RingItem changeSize(RingSizeVO newSize) {
        return new RingItem(this.id, newSize, this.style, this.price, this.materials, this.gemstones, this.description);
    }
}
