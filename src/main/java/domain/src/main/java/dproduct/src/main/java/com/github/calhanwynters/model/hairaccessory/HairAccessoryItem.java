package com.github.calhanwynters.model.hairaccessory;

import com.github.calhanwynters.model.shared.valueobjects.DescriptionVO;
import com.github.calhanwynters.model.shared.valueobjects.GemstoneVO;
import com.github.calhanwynters.model.shared.valueobjects.MaterialCompositionVO;
import com.github.calhanwynters.model.shared.valueobjects.PriceVO;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Aggregate Root representing a Hair Accessory item in the domain.
 * An immutable record that controls access to its internal components
 * and enforces business invariants.
 */
public record HairAccessoryItem(
        HairAccessoryId id,
        HairAccessorStyleVO style,
        HairAccessorySizeVO size,
        PriceVO price,
        Set<MaterialCompositionVO> materials, // Updated from single MaterialVO
        Set<GemstoneVO> gemstones,           // Added gemstones as accessories might have them
        DescriptionVO description
) {
    // Compact constructor to ensure all fields are non-null
    public HairAccessoryItem {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(style, "style must not be null");
        Objects.requireNonNull(size, "size must not be null");
        Objects.requireNonNull(price, "price must not be null");
        Objects.requireNonNull(materials, "materials must not be null");
        Objects.requireNonNull(gemstones, "gemstones must not be null");
        Objects.requireNonNull(description, "description must not be null");

        // Ensure collections are unmodifiable copies
        materials = Set.copyOf(materials);
        gemstones = Set.copyOf(gemstones);
    }

    // Factory method for creating a new hair accessory from required VOs
    public static HairAccessoryItem create(
            HairAccessorStyleVO style,
            HairAccessorySizeVO size,
            PriceVO price,
            Set<MaterialCompositionVO> materials, // Updated parameter
            DescriptionVO description
    ) {
        // Assume no gemstones initially for the simple create factory
        return new HairAccessoryItem(HairAccessoryId.generate(), style, size, price, materials, Collections.emptySet(), description);
    }

    // --- Domain Behaviors (returning new instances for immutability) ---

    public HairAccessoryItem changePrice(PriceVO newPrice) {
        return new HairAccessoryItem(this.id, this.style, this.size, newPrice, this.materials, this.gemstones, this.description);
    }

    public HairAccessoryItem changeSize(HairAccessorySizeVO newSize) {
        return new HairAccessoryItem(this.id, this.style, newSize, this.price, this.materials, this.gemstones, this.description);
    }

    public HairAccessoryItem changeDescription(DescriptionVO newDescription) {
        return new HairAccessoryItem(this.id, this.style, this.size, this.price, this.materials, this.gemstones, newDescription);
    }

    /**
     * Adds a material composition to the accessory.
     */
    public HairAccessoryItem addMaterial(MaterialCompositionVO material) {
        Set<MaterialCompositionVO> newMaterials = new HashSet<>(this.materials);
        newMaterials.add(material);
        return new HairAccessoryItem(this.id, this.style, this.size, this.price, newMaterials, this.gemstones, this.description);
    }

    /**
     * Adds a gemstone to the accessory.
     */
    public HairAccessoryItem addGemstone(GemstoneVO gemstone) {
        Set<GemstoneVO> newGemstones = new HashSet<>(this.gemstones);
        newGemstones.add(gemstone);
        return new HairAccessoryItem(this.id, this.style, this.size, this.price, this.materials, newGemstones, this.description);
    }
}
