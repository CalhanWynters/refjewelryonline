package com.github.calhanwynters.model.shared.aggregates;

import com.github.calhanwynters.model.shared.entities.Variant; // Import the shared interface
import com.github.calhanwynters.model.shared.valueobjects.*;
import java.util.*;

/*** Aggregate Root representing a Product in the domain.
 * An immutable record that controls access to its internal components
 * and enforces business invariants.*/

public record Product(
        ProductId id,
        DescriptionVO description,
        GalleryVO gallery,
        Set<Variant> variants // Now uses the generic 'Variant' interface
) {
    public Product {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(description, "description must not be null");
        Objects.requireNonNull(gallery, "gallery must not be null");
        Objects.requireNonNull(variants, "variants must not be null");

        // Ensure the internal collection is deeply immutable
        variants = Set.copyOf(variants);
    }

    // Updated Factory method: Now accepts initial variants as the generic interface
    public static Product create(
            DescriptionVO description,
            GalleryVO gallery,
            Set<Variant> initialVariants // Accepts the interface
    ) {
        return new Product(ProductId.generate(), description, gallery, initialVariants);
    }

    // Factory method for creating an item with no variants initially
    public static Product create(
            DescriptionVO description,
            GalleryVO gallery
    ) {
        return create(description, gallery, Collections.emptySet());
    }

    // --- Behavior Methods ---

    public Product changeDescription(DescriptionVO newDescription) {
        // Corrected to return a new instance with the updated description (using the correct class name)
        return new Product(this.id, newDescription, this.gallery, this.variants);
    }

    public Product addImage(ImageUrlVO newImageUrl) {
        Set<ImageUrlVO> updatedImages = new HashSet<>(this.gallery.images());
        updatedImages.add(newImageUrl);
        GalleryVO updatedGallery = new GalleryVO(updatedImages);
        // Corrected to use the updated gallery
        return new Product(this.id, this.description, updatedGallery, this.variants);
    }

    /**
     * Adds a new variant to the Product.
     * @param newVariant The variant to add (can be any type that implements the interface).
     * @return A new Product instance with the added variant.
     */
    public Product addVariant(Variant newVariant) { // Accepts the interface
        Objects.requireNonNull(newVariant, "newVariant must not be null");
        Set<Variant> updatedVariants = new HashSet<>(this.variants);
        if (!updatedVariants.add(newVariant)) {
            throw new IllegalArgumentException("Variant with this ID already exists.");
        }
        return new Product(this.id, this.description, this.gallery, updatedVariants);
    }

    /**
     * Finds a variant by its ID.
     * @param variantId The ID to search for.
     * @return An Optional containing the variant, if found within this aggregate.
     */
    public Optional<Variant> findVariantById(VariantId variantId) { // Uses the generic VariantId
        return this.variants.stream()
                .filter(v -> v.id().equals(variantId))
                .findFirst();
    }
}
