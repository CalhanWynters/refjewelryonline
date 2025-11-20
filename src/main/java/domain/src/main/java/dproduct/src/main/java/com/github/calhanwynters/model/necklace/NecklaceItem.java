package com.github.calhanwynters.model.necklace;

import com.github.calhanwynters.model.shared.valueobjects.*;

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
        Set<MaterialCompositionVO> materials,
        Set<GemstoneVO> gemstones,
        DescriptionVO description,
        CareInstructionVO careInstructions, // Added comma separator
        GalleryVO gallery // Added the gallery field
) {
    // Compact constructor to ensure all fields are non-null and collections are immutable
    public NecklaceItem {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(size, "size must not be null");
        Objects.requireNonNull(style, "style must not be null");
        Objects.requireNonNull(price, "price must not be null");
        Objects.requireNonNull(materials, "materials must not be null");
        Objects.requireNonNull(description, "description must not be null");
        Objects.requireNonNull(gemstones, "gemstones must not be null");
        Objects.requireNonNull(careInstructions, "careInstructions must not be null");
        Objects.requireNonNull(gallery, "gallery must not be null"); // Added null check

        // Ensure the internal sets are unmodifiable copies
        materials = Set.copyOf(materials);
        gemstones = Set.copyOf(gemstones);
    }

    // Factory method for creating a new necklace from required VOs
    public static NecklaceItem create(
            NecklaceSizeVO size,
            NecklaceStyleVO style,
            PriceVO price,
            Set<MaterialCompositionVO> materials,
            DescriptionVO description,
            CareInstructionVO careInstructions, // Added parameter
            GalleryVO gallery // Added parameter
    ) {
        return new NecklaceItem(NecklaceId.generate(), size, style, price, materials, Collections.emptySet(), description, careInstructions, gallery);
    }

    // --- Domain Behaviors (returning new instances for immutability) ---

    public NecklaceItem changePrice(PriceVO newPrice) {
        return new NecklaceItem(this.id, this.size, this.style, newPrice, this.materials, this.gemstones, this.description, this.careInstructions, this.gallery);
    }

    public NecklaceItem changeDescription(DescriptionVO newDescription) {
        return new NecklaceItem(this.id, this.size, this.style, this.price, this.materials, this.gemstones, newDescription, this.careInstructions, this.gallery);
    }

    public NecklaceItem addGemstone(GemstoneVO gemstone) {
        Set<GemstoneVO> newGemstones = new HashSet<>(this.gemstones);
        newGemstones.add(gemstone);
        return new NecklaceItem(this.id, this.size, this.style, this.price, this.materials, newGemstones, this.description, this.careInstructions, this.gallery);
    }

    /**
     * Adds a material composition to the necklace.
     * @param material The material composition value object to add.
     * @return A new NecklaceItem instance with the new material.
     */
    public NecklaceItem addMaterial(MaterialCompositionVO material) {
        Set<MaterialCompositionVO> newMaterials = new HashSet<>(this.materials);
        newMaterials.add(material);
        return new NecklaceItem(this.id, this.size, this.style, this.price, newMaterials, this.gemstones, this.description, this.careInstructions, this.gallery);
    }

    /**
     * Updates the care instructions (e.g., after expert consultation).
     * @param newInstructions The updated care instructions.
     * @return A new NecklaceItem instance with updated instructions.
     */
    public NecklaceItem changeCareInstructions(CareInstructionVO newInstructions) {
        return new NecklaceItem(this.id, this.size, this.style, this.price, this.materials, this.gemstones, this.description, newInstructions, this.gallery);
    }

    /**
     * Adds an image to the gallery and returns a new NecklaceItem with the updated gallery VO.
     */
    public NecklaceItem addImage(ImageUrlVO newImageUrl) {
        Set<ImageUrlVO> updatedImages = new HashSet<>(this.gallery.images());
        updatedImages.add(newImageUrl);
        GalleryVO updatedGallery = new GalleryVO(updatedImages); // GalleryVO handles validation rules

        return new NecklaceItem(this.id, this.size, this.style, this.price, this.materials,
                this.gemstones, this.description, this.careInstructions, updatedGallery);
    }
}
