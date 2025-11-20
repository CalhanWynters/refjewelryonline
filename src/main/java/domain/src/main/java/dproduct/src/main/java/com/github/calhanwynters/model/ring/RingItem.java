package com.github.calhanwynters.model.ring;

import com.github.calhanwynters.model.shared.valueobjects.CareInstructionVO;
import com.github.calhanwynters.model.shared.valueobjects.DescriptionVO;
import com.github.calhanwynters.model.shared.valueobjects.GalleryVO; // Added import
import com.github.calhanwynters.model.shared.valueobjects.GemstoneVO;
import com.github.calhanwynters.model.shared.valueobjects.ImageUrlVO; // Added import for new behavior
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
        CareInstructionVO careInstructions,
        GalleryVO gallery // <-- Explicitly added the gallery field
) {
    public RingItem {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(size, "size must not be null");
        Objects.requireNonNull(style, "style must not be null");
        Objects.requireNonNull(price, "price must not be null");
        Objects.requireNonNull(materials, "materials must not be null");
        Objects.requireNonNull(description, "description must not be null");
        Objects.requireNonNull(gemstones, "gemstones must not be null");
        Objects.requireNonNull(careInstructions, "careInstructions must not be null");
        Objects.requireNonNull(gallery, "gallery must not be null"); // Added check for the new field

        materials = Set.copyOf(materials);
        gemstones = Set.copyOf(gemstones);
    }

    public static RingItem create(
            RingSizeVO size,
            RingStyleVO style,
            PriceVO price,
            Set<MaterialCompositionVO> materials,
            DescriptionVO description,
            CareInstructionVO careInstructions,
            GalleryVO gallery // Added parameter
    ) {
        return new RingItem(RingId.generate(), size, style, price, materials, Collections.emptySet(), description, careInstructions, gallery);
    }

    // --- Domain Behaviors (returning new instances for immutability) ---

    public RingItem changePrice(PriceVO newPrice) {
        return new RingItem(this.id, this.size, this.style, newPrice, this.materials, this.gemstones, this.description, this.careInstructions, this.gallery);
    }

    public RingItem changeDescription(DescriptionVO newDescription) {
        return new RingItem(this.id, this.size, this.style, this.price, this.materials, this.gemstones, newDescription, this.careInstructions, this.gallery);
    }

    public RingItem addGemstone(GemstoneVO gemstone) {
        Set<GemstoneVO> newGemstones = new HashSet<>(this.gemstones);
        newGemstones.add(gemstone);
        return new RingItem(this.id, this.size, this.style, this.price, this.materials, newGemstones, this.description, this.careInstructions, this.gallery);
    }

    public RingItem addMaterial(MaterialCompositionVO newMaterial) {
        Set<MaterialCompositionVO> newMaterials = new HashSet<>(this.materials);
        newMaterials.add(newMaterial);
        return new RingItem(this.id, this.size, this.style, this.price, newMaterials, this.gemstones, this.description, this.careInstructions, this.gallery);
    }

    public RingItem changeSize(RingSizeVO newSize) {
        return new RingItem(this.id, newSize, this.style, this.price, this.materials, this.gemstones, this.description, this.careInstructions, this.gallery);
    }

    public RingItem changeCareInstructions(CareInstructionVO newInstructions) {
        return new RingItem(this.id, this.size, this.style, this.price, this.materials, this.gemstones, this.description, newInstructions, this.gallery);
    }

    /**
     * Adds an image to the gallery and returns a new RingItem with the updated gallery VO.
     */
    public RingItem addImage(ImageUrlVO newImageUrl) {
        Set<ImageUrlVO> updatedImages = new HashSet<>(this.gallery.images());
        updatedImages.add(newImageUrl);
        GalleryVO updatedGallery = new GalleryVO(updatedImages);

        return new RingItem(this.id, this.size, this.style, this.price, this.materials,
                this.gemstones, this.description, this.careInstructions, updatedGallery);
    }
}
