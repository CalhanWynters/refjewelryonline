package com.github.calhanwynters.model.shared.entities;

import com.github.calhanwynters.model.hairaccessoryattributes.HairAccessorStyleVO;
import com.github.calhanwynters.model.hairaccessoryattributes.HairAccessorySizeVO;
import com.github.calhanwynters.model.shared.enums.VariantStatusVO;
import com.github.calhanwynters.model.shared.valueobjects.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.Collections;

public record HairAccessoryVariant(
        VariantId id,
        String sku, // Added SKU field
        HairAccessorySizeVO size,
        HairAccessorStyleVO style,
        PriceVO basePrice, // Changed 'price' to 'basePrice'
        PriceVO currentPrice, // Added 'currentPrice'
        Set<MaterialCompositionVO> materials,
        Set<GemstoneVO> gemstones,
        CareInstructionVO careInstructions,
        VariantStatusVO status // Added status field
) implements Variant {

    public HairAccessoryVariant {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(sku, "sku must not be null");
        Objects.requireNonNull(size, "size must not be null");
        Objects.requireNonNull(style, "style must not be null");
        Objects.requireNonNull(basePrice, "basePrice must not be null");
        Objects.requireNonNull(currentPrice, "currentPrice must not be null");
        Objects.requireNonNull(materials, "materials must not be null");
        Objects.requireNonNull(gemstones, "gemstones must not be null");
        Objects.requireNonNull(careInstructions, "careInstructions must not be null");
        Objects.requireNonNull(status, "status must not be null");

        // Enforce business invariant: variant must have at least one material
        if (materials.isEmpty()) {
            throw new IllegalArgumentException("Hair accessory variant must have at least one material composition.");
        }

        // Use Set.copyOf() in the compact constructor for immutability
        materials = Set.copyOf(materials);
        gemstones = Set.copyOf(gemstones);
    }

    public static HairAccessoryVariant create(
            HairAccessorySizeVO size,
            HairAccessorStyleVO style,
            PriceVO basePrice, // Use basePrice in create method
            Set<MaterialCompositionVO> materials,
            CareInstructionVO careInstructions
    ) {
        VariantId generatedId = VariantId.generate();
        String generatedSku = "HAIR-" + generatedId.value().substring(0, 8).toUpperCase(); // Generate SKU

        // We use the shared VariantId.generate() here
        return new HairAccessoryVariant(
                generatedId,
                generatedSku,
                size,
                style,
                basePrice,
                basePrice, // currentPrice defaults to basePrice
                materials,
                Set.of(),
                careInstructions,
                VariantStatusVO.DRAFT // Default status to DRAFT
        );
    }

    // --- New Behavior: Variant Comparison Logic (added for consistency) ---

    @Override
    public boolean hasSameAttributes(Variant other) {
        if (!(other instanceof HairAccessoryVariant otherHairAccessory)) {
            return false; // Not the same *type* of variant
        }
        // Compare all significant physical/descriptive attributes
        return Objects.equals(this.size, otherHairAccessory.size()) &&
                Objects.equals(this.style, otherHairAccessory.style()) &&
                Objects.equals(this.materials, otherHairAccessory.materials()) &&
                Objects.equals(this.gemstones, otherHairAccessory.gemstones()) &&
                Objects.equals(this.careInstructions, otherHairAccessory.careInstructions());
    }

    // --- Behavior Methods ---

    public HairAccessoryVariant changeBasePrice(PriceVO newBasePrice) {
        // Enforce the invariant: When base price changes, current price must match it.
        return new HairAccessoryVariant(this.id, this.sku, this.size, this.style, newBasePrice, newBasePrice, this.materials, this.gemstones, this.careInstructions, this.status);
    }

    public HairAccessoryVariant changeCurrentPrice(PriceVO newCurrentPrice) {
        // This method is used for manual price adjustments or discounts.
        return new HairAccessoryVariant(this.id, this.sku, this.size, this.style, this.basePrice, newCurrentPrice, this.materials, this.gemstones, this.careInstructions, this.status);
    }

    /**
     * Applies a percentage discount to the current price.
     */
    public HairAccessoryVariant applyDiscount(PercentageVO discount) {
        BigDecimal discountedAmount = this.basePrice.amount().multiply(BigDecimal.ONE.subtract(discount.value()));
        PriceVO discountedPrice = new PriceVO(discountedAmount, this.basePrice.currency());
        return this.changeCurrentPrice(discountedPrice);
    }

    /**
     * Removes the applied discount and reverts the price to the base price.
     */
    public HairAccessoryVariant removeDiscount() {
        return this.changeCurrentPrice(this.basePrice);
    }

    public HairAccessoryVariant addGemstone(GemstoneVO gemstone) {
        Set<GemstoneVO> newGemstones = new HashSet<>(this.gemstones);
        newGemstones.add(gemstone);
        return new HairAccessoryVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.materials, newGemstones, this.careInstructions, this.status);
    }

    /**
     * Removes a gemstone from the variant.
     */
    public HairAccessoryVariant removeGemstone(GemstoneVO gemstone) {
        Set<GemstoneVO> newGemstones = new HashSet<>(this.gemstones);
        if (newGemstones.remove(gemstone)) {
            return new HairAccessoryVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.materials, newGemstones, this.careInstructions, this.status);
        }
        return this;
    }

    /**
     * Adds a material composition.
     */
    public HairAccessoryVariant addMaterial(MaterialCompositionVO material) {
        Set<MaterialCompositionVO> newMaterials = new HashSet<>(this.materials);
        newMaterials.add(material);
        return new HairAccessoryVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, newMaterials, this.gemstones, this.careInstructions, this.status);
    }

    /**
     * Removes a material composition.
     */
    public HairAccessoryVariant removeMaterial(MaterialCompositionVO material) {
        Set<MaterialCompositionVO> newMaterials = new HashSet<>(this.materials);
        if (newMaterials.remove(material)) {
            // Must re-validate that the resulting set is not empty before creating the new instance
            if (newMaterials.isEmpty()) {
                throw new IllegalStateException("Hair accessory variant must have at least one material composition; cannot remove the last one.");
            }
            return new HairAccessoryVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, newMaterials, this.gemstones, this.careInstructions, this.status);
        }
        return this;
    }

    /**
     * Updates the care instructions.
     */
    public HairAccessoryVariant changeCareInstructions(CareInstructionVO newInstructions) {
        return new HairAccessoryVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.materials, this.gemstones, newInstructions, this.status);
    }

    // --- Lifecycle/Status Behavior Methods (added for consistency) ---

    public boolean isActive() {
        return this.status == VariantStatusVO.ACTIVE;
    }

    public HairAccessoryVariant activate() {
        if (this.status == VariantStatusVO.DISCONTINUED) {
            throw new IllegalStateException("Cannot activate a discontinued variant.");
        }
        return new HairAccessoryVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.materials, this.gemstones, this.careInstructions, VariantStatusVO.ACTIVE);
    }

    public HairAccessoryVariant deactivate() {
        return new HairAccessoryVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.materials, this.gemstones, this.careInstructions, VariantStatusVO.INACTIVE);
    }

    public HairAccessoryVariant markAsDiscontinued() {
        return new HairAccessoryVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.materials, this.gemstones, this.careInstructions, VariantStatusVO.DISCONTINUED);
    }
}
