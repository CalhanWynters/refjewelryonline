package com.github.calhanwynters.model.anklet;

import com.github.calhanwynters.model.shared.valueobjects.*;

import java.util.*;

/**
 * Aggregate Root representing an Anklet item in the domain.
 * An immutable record that controls access to its internal components
 * and enforces business invariants.
 */
public record AnkletItem(
        AnkletId id,
        AnkletSizeVO size,
        AnkletStyleVO style,
        PriceVO price,
        Set<MaterialCompositionVO> materials,
        Set<GemstoneVO> gemstones,
        DescriptionVO description,
        CareInstructionVO careInstructions,
        GalleryVO gallery // Added gallery field
) {
    public AnkletItem {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(size, "size must not be null");
        Objects.requireNonNull(style, "style must not be null");
        Objects.requireNonNull(price, "price must not be null");
        Objects.requireNonNull(materials, "materials must not be null");
        Objects.requireNonNull(gemstones, "gemstones must not be null");
        Objects.requireNonNull(description, "description must not be null");
        Objects.requireNonNull(careInstructions, "careInstructions must not be null"); // Check the new field
        Objects.requireNonNull(gallery, "gallery must not be null"); // Check for the new gallery field

        materials = Set.copyOf(materials);
        gemstones = Set.copyOf(gemstones);
    }

    // Updated Factory method: Now accepts the gallery as a parameter
    public static AnkletItem create(
            AnkletSizeVO size,
            AnkletStyleVO style,
            PriceVO price,
            Set<MaterialCompositionVO> materials,
            DescriptionVO description,
            CareInstructionVO careInstructions,
            GalleryVO gallery // Added gallery parameter
    ) {
        return new AnkletItem(AnkletId.generate(), size, style, price, materials, Collections.emptySet(), description, careInstructions, gallery);
    }

    // --- Behavior Methods: Update existing to include careInstructions and gallery ---

    public AnkletItem changePrice(PriceVO newPrice) {
        return new AnkletItem(this.id, this.size, this.style, newPrice, this.materials, this.gemstones, this.description, this.careInstructions, this.gallery);
    }

    public AnkletItem changeDescription(DescriptionVO newDescription) {
        return new AnkletItem(this.id, this.size, this.style, this.price, this.materials, this.gemstones, newDescription, this.careInstructions, this.gallery);
    }

    public AnkletItem addGemstone(GemstoneVO gemstone) {
        Set<GemstoneVO> newGemstones = new HashSet<>(this.gemstones);
        newGemstones.add(gemstone);
        return new AnkletItem(this.id, this.size, this.style, this.price, this.materials, newGemstones, this.description, this.careInstructions, this.gallery);
    }

    /**
     * Adds a material composition. Instructions are NOT recalculated.
     */
    public AnkletItem addMaterial(MaterialCompositionVO material) {
        Set<MaterialCompositionVO> newMaterials = new HashSet<>(this.materials);
        newMaterials.add(material);
        // Pass existing instructions through
        return new AnkletItem(this.id, this.size, this.style, this.price, newMaterials, this.gemstones, this.description, this.careInstructions, this.gallery);
    }

    /**
     * Updates the care instructions (e.g., after expert consultation).
     * @param newInstructions The updated care instructions.
     * @return A new AnkletItem instance with updated instructions.
     */
    public AnkletItem changeCareInstructions(CareInstructionVO newInstructions) {
        return new AnkletItem(this.id, this.size, this.style, this.price, this.materials, this.gemstones, this.description, newInstructions, this.gallery);
    }

    // Add method to update the gallery if needed
    public AnkletItem addImage(ImageUrlVO newImageUrl) {
        Set<ImageUrlVO> updatedImages = new HashSet<>(this.gallery.images());
        updatedImages.add(newImageUrl);
        GalleryVO updatedGallery = new GalleryVO(updatedImages); // Ensure the gallery handles validation rules
        return new AnkletItem(this.id, this.size, this.style, this.price, this.materials, this.gemstones, this.description, this.careInstructions, updatedGallery);
    }
}
