package com.github.calhanwynters.model.anklet;

import com.github.calhanwynters.model.shared.valueobjects.*;
import java.util.*;

/*** Aggregate Root representing an Anklet item in the domain.
 * An immutable record that controls access to its internal components
 * and enforces business invariants.*/
public record AnkletItem(
        AnkletId id,
        DescriptionVO description,
        GalleryVO gallery,
        Set<AnkletVariant> variants
) {
    public AnkletItem {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(description, "description must not be null");
        Objects.requireNonNull(gallery, "gallery must not be null");
        Objects.requireNonNull(variants, "variants must not be null");

        // Ensure the internal collection is deeply immutable
        variants = Set.copyOf(variants);
    }

    // Updated Factory method: Now accepts initial variants
    public static AnkletItem create(
            DescriptionVO description,
            GalleryVO gallery,
            Set<AnkletVariant> initialVariants
    ) {
        return new AnkletItem(AnkletId.generate(), description, gallery, initialVariants);
    }

    // Factory method for creating an item with no variants initially
    public static AnkletItem create(
            DescriptionVO description,
            GalleryVO gallery
    ) {
        return create(description, gallery, Collections.emptySet());
    }

    // --- Behavior Methods ---

    public AnkletItem changeDescription(DescriptionVO newDescription) {
        // Corrected to return a new instance with the updated description
        return new AnkletItem(this.id, newDescription, this.gallery, this.variants);
    }

    public AnkletItem addImage(ImageUrlVO newImageUrl) {
        Set<ImageUrlVO> updatedImages = new HashSet<>(this.gallery.images());
        updatedImages.add(newImageUrl);
        GalleryVO updatedGallery = new GalleryVO(updatedImages);
        // Corrected to use the updated gallery
        return new AnkletItem(this.id, this.description, updatedGallery, this.variants);
    }

    /**
     * Adds a new variant to the AnkletItem.
     * @param newVariant The variant to add.
     * @return A new AnkletItem instance with the added variant.
     */
    public AnkletItem addVariant(AnkletVariant newVariant) {
        Objects.requireNonNull(newVariant, "newVariant must not be null");
        Set<AnkletVariant> updatedVariants = new HashSet<>(this.variants);
        if (!updatedVariants.add(newVariant)) {
            throw new IllegalArgumentException("Variant with this ID already exists.");
        }
        return new AnkletItem(this.id, this.description, this.gallery, updatedVariants);
    }

    /**
     * Finds a variant by its ID.
     * @param variantId The ID to search for.
     * @return An Optional containing the variant, if found within this aggregate.
     */
    public Optional<AnkletVariant> findVariantById(AnkletVariantId variantId) {
        return this.variants.stream()
                .filter(v -> v.id().equals(variantId))
                .findFirst();
    }
}
