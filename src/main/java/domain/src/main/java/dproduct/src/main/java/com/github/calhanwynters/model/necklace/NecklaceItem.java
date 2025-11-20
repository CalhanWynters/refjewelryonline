package com.github.calhanwynters.model.necklace;

import com.github.calhanwynters.model.shared.valueobjects.DescriptionVO;
import com.github.calhanwynters.model.shared.valueobjects.GemstoneVO;
import com.github.calhanwynters.model.shared.valueobjects.MaterialCompositionVO;
import com.github.calhanwynters.model.shared.valueobjects.PriceVO;

import java.util.*;

/**
 * Aggregate Root representing a Necklace item in the domain.
 * An immutable record that controls access to its internal components
 * and enforces business invariants.
 */
public record NecklaceItem(
        NecklaceId id,
        NecklaceSizeVO size,
        NecklaceStyleVO style,
        PriceVO price,
        Set<MaterialCompositionVO> materials, // Updated from single MaterialVO to Set
        Set<GemstoneVO> gemstones,
        DescriptionVO description
) {
    // Compact constructor to ensure all fields are non-null and collections are immutable
    public NecklaceItem {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(size, "size must not be null");
        Objects.requireNonNull(style, "style must not be null");
        Objects.requireNonNull(price, "price must not be null");
        Objects.requireNonNull(materials, "materials must not be null"); // Check the new field
        Objects.requireNonNull(description, "description must not be null");
        Objects.requireNonNull(gemstones, "gemstones must not be null");

        // Ensure the internal sets are unmodifiable copies
        materials = Set.copyOf(materials);
        gemstones = Set.copyOf(gemstones);
    }

    // Factory method for creating a new necklace from required VOs
    public static NecklaceItem create(
            NecklaceSizeVO size,
            NecklaceStyleVO style,
            PriceVO price,
            Set<MaterialCompositionVO> materials, // Updated parameter
            DescriptionVO description
    ) {
        return new NecklaceItem(NecklaceId.generate(), size, style, price, materials, Collections.emptySet(), description);
    }

    // --- Domain Behaviors (returning new instances for immutability) ---

    public NecklaceItem changePrice(PriceVO newPrice) {
        return new NecklaceItem(this.id, this.size, this.style, newPrice, this.materials, this.gemstones, this.description);
    }

    public NecklaceItem changeDescription(DescriptionVO newDescription) {
        return new NecklaceItem(this.id, this.size, this.style, this.price, this.materials, this.gemstones, newDescription);
    }

    public NecklaceItem addGemstone(GemstoneVO gemstone) {
        Set<GemstoneVO> newGemstones = new HashSet<>(this.gemstones);
        newGemstones.add(gemstone);
        return new NecklaceItem(this.id, this.size, this.style, this.price, this.materials, newGemstones, this.description);
    }

    /**
     * Adds a material composition to the necklace.
     * @param material The material composition value object to add.
     * @return A new NecklaceItem instance with the new material.
     */
    public NecklaceItem addMaterial(MaterialCompositionVO material) {
        Set<MaterialCompositionVO> newMaterials = new HashSet<>(this.materials);
        newMaterials.add(material);
        return new NecklaceItem(this.id, this.size, this.style, this.price, newMaterials, this.gemstones, this.description);
    }
}
