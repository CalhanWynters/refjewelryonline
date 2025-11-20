package com.github.calhanwynters.model.anklet;

import com.github.calhanwynters.model.shared.valueobjects.DescriptionVO;
import com.github.calhanwynters.model.shared.valueobjects.GemstoneVO;
import com.github.calhanwynters.model.shared.valueobjects.MaterialCompositionVO; // Added import
import com.github.calhanwynters.model.shared.valueobjects.PriceVO;

import java.util.*;

/**
 * Aggregate Root representing an Anklet in the domain.
 * An immutable record that controls access to its internal components
 * and enforces business invariants.
 */
public record AnkletItem(
        AnkletId id,
        AnkletSizeVO size,
        AnkletStyleVO style,
        PriceVO price,
        Set<MaterialCompositionVO> materials, // Updated from single MaterialVO
        Set<GemstoneVO> gemstones,
        DescriptionVO description
) {
    // Compact constructor to ensure all fields are non-null
    public AnkletItem {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(size, "size must not be null");
        Objects.requireNonNull(style, "style must not be null");
        Objects.requireNonNull(price, "price must not be null");
        Objects.requireNonNull(materials, "materials must not be null"); // Check the new field
        Objects.requireNonNull(gemstones, "gemstones must not be null");
        Objects.requireNonNull(description, "description must not be null");

        // Ensure the internal sets are unmodifiable copies
        materials = Set.copyOf(materials);
        gemstones = Set.copyOf(gemstones);
    }

    // Factory method for creating a new anklet from required VOs
    public static AnkletItem create(
            AnkletSizeVO size,
            AnkletStyleVO style,
            PriceVO price,
            Set<MaterialCompositionVO> materials, // Updated parameter
            DescriptionVO description
    ) {
        // Uses the generate() factory method
        return new AnkletItem(AnkletId.generate(), size, style, price, materials, Collections.emptySet(), description);
    }

    // --- Domain Behaviors (returning new instances) ---

    /**
     * Changes the price of the anklet.
     * @param newPrice The new price value object.
     * @return A new AnkletItem instance with the updated price.
     */
    public AnkletItem changePrice(PriceVO newPrice) {
        return new AnkletItem(this.id, this.size, this.style, newPrice, this.materials, this.gemstones, this.description);
    }

    /**
     * Adds a gemstone to the anklet.
     * @param gemstone The gemstone value object to add.
     * @return A new AnkletItem instance with the new gemstone.
     */
    public AnkletItem addGemstone(GemstoneVO gemstone) {
        Set<GemstoneVO> newGemstones = new HashSet<>(this.gemstones);
        newGemstones.add(gemstone);
        return new AnkletItem(this.id, this.size, this.style, this.price, this.materials, newGemstones, this.description);
    }

    /**
     * Adds a material composition to the anklet.
     * @param newMaterial The new material composition value object.
     * @return A new AnkletItem instance with the updated material.
     */
    public AnkletItem addMaterial(MaterialCompositionVO newMaterial) {
        Set<MaterialCompositionVO> newMaterials = new HashSet<>(this.materials);
        newMaterials.add(newMaterial);
        return new AnkletItem(this.id, this.size, this.style, this.price, newMaterials, this.gemstones, this.description);
    }

    /**
     * Updates the description of the anklet.
     * @param newDescription The new description value object.
     * @return A new AnkletItem instance with the updated description.
     */
    public AnkletItem changeDescription(DescriptionVO newDescription) {
        return new AnkletItem(this.id, this.size, this.style, this.price, this.materials, this.gemstones, newDescription);
    }
}
