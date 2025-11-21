package com.github.calhanwynters.model.shared.entities;

import com.github.calhanwynters.model.ringattributes.RingSizeVO;
import com.github.calhanwynters.model.ringattributes.RingStyleVO;
import com.github.calhanwynters.model.shared.enums.VariantStatusVO;
import com.github.calhanwynters.model.shared.valueobjects.CareInstructionVO;
import com.github.calhanwynters.model.shared.valueobjects.GemstoneVO;
import com.github.calhanwynters.model.shared.valueobjects.MaterialCompositionVO;
import com.github.calhanwynters.model.shared.valueobjects.PercentageVO;
import com.github.calhanwynters.model.shared.valueobjects.PriceVO;
import com.github.calhanwynters.model.shared.valueobjects.VariantId;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public record RingVariant(
        VariantId id, // Using the shared VariantId from shared.valueobjects
        String sku, // Added SKU field
        RingSizeVO size, // Specific VO
        RingStyleVO style, // Specific VO
        PriceVO basePrice, // Changed 'price' to 'basePrice'
        PriceVO currentPrice, // Added 'currentPrice'
        Set<MaterialCompositionVO> materials,
        Set<GemstoneVO> gemstones,
        CareInstructionVO careInstructions,
        VariantStatusVO status // Added status field
) implements Variant {

    public RingVariant {
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
            throw new IllegalArgumentException("Ring variant must have at least one material composition.");
        }

        // Use Set.copyOf() in the compact constructor for immutability
        materials = Set.copyOf(materials);
        gemstones = Set.copyOf(gemstones);
    }

    public static RingVariant create(
            RingSizeVO size,
            RingStyleVO style,
            PriceVO basePrice, // Use basePrice in create method
            Set<MaterialCompositionVO> materials,
            CareInstructionVO careInstructions
    ) {
        VariantId generatedId = VariantId.generate();
        String generatedSku = "RING-" + generatedId.value().substring(0, 8).toUpperCase(); // Generate SKU

        return new RingVariant(
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
        if (!(other instanceof RingVariant otherRing)) {
            return false; // Not the same *type* of variant
        }
        // Compare all significant physical/descriptive attributes
        return Objects.equals(this.size, otherRing.size()) &&
                Objects.equals(this.style, otherRing.style()) &&
                Objects.equals(this.materials, otherRing.materials()) &&
                Objects.equals(this.gemstones, otherRing.gemstones()) &&
                Objects.equals(this.careInstructions, otherRing.careInstructions());
    }

    // --- Behavior Methods ---

    public RingVariant changeBasePrice(PriceVO newBasePrice) {
        // Enforce the invariant: When base price changes, current price must match it.
        return new RingVariant(this.id, this.sku, this.size, this.style, newBasePrice, newBasePrice, this.materials, this.gemstones, this.careInstructions, this.status);
    }

    public RingVariant changeCurrentPrice(PriceVO newCurrentPrice) {
        // This method is used for manual price adjustments or discounts.
        return new RingVariant(this.id, this.sku, this.size, this.style, this.basePrice, newCurrentPrice, this.materials, this.gemstones, this.careInstructions, this.status);
    }

    /**
     * Applies a percentage discount to the current price.
     */
    public RingVariant applyDiscount(PercentageVO discount) {
        BigDecimal discountedAmount = this.basePrice.amount().multiply(BigDecimal.ONE.subtract(discount.value()));
        PriceVO discountedPrice = new PriceVO(discountedAmount, this.basePrice.currency());
        return this.changeCurrentPrice(discountedPrice);
    }

    /**
     * Removes the applied discount and reverts the price to the base price.
     */
    public RingVariant removeDiscount() {
        return this.changeCurrentPrice(this.basePrice);
    }

    public RingVariant addGemstone(GemstoneVO gemstone) {
        Set<GemstoneVO> newGemstones = new HashSet<>(this.gemstones);
        newGemstones.add(gemstone);
        return new RingVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.materials, newGemstones, this.careInstructions, this.status);
    }

    /**
     * Removes a gemstone from the variant.
     */
    public RingVariant removeGemstone(GemstoneVO gemstone) {
        Set<GemstoneVO> newGemstones = new HashSet<>(this.gemstones);
        if (newGemstones.remove(gemstone)) {
            return new RingVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.materials, newGemstones, this.careInstructions, this.status);
        }
        return this;
    }

    /**
     * Adds a material composition.
     */
    public RingVariant addMaterial(MaterialCompositionVO material) {
        Set<MaterialCompositionVO> newMaterials = new HashSet<>(this.materials);
        newMaterials.add(material);
        return new RingVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, newMaterials, this.gemstones, this.careInstructions, this.status);
    }

    /**
     * Removes a material composition.
     */
    public RingVariant removeMaterial(MaterialCompositionVO material) {
        Set<MaterialCompositionVO> newMaterials = new HashSet<>(this.materials);
        if (newMaterials.remove(material)) {
            // Must re-validate that the resulting set is not empty before creating the new instance
            if (newMaterials.isEmpty()) {
                throw new IllegalStateException("Ring variant must have at least one material composition; cannot remove the last one.");
            }
            return new RingVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, newMaterials, this.gemstones, this.careInstructions, this.status);
        }
        return this;
    }

    /**
     * Updates the care instructions.
     */
    public RingVariant changeCareInstructions(CareInstructionVO newInstructions) {
        return new RingVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.materials, this.gemstones, newInstructions, this.status);
    }

    // --- Lifecycle/Status Behavior Methods (added for consistency) ---

    public boolean isActive() {
        return this.status == VariantStatusVO.ACTIVE;
    }

    public RingVariant activate() {
        if (this.status == VariantStatusVO.DISCONTINUED) {
            throw new IllegalStateException("Cannot activate a discontinued variant.");
        }
        return new RingVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.materials, this.gemstones, this.careInstructions, VariantStatusVO.ACTIVE);
    }

    public RingVariant deactivate() {
        return new RingVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.materials, this.gemstones, this.careInstructions, VariantStatusVO.INACTIVE);
    }

    public RingVariant markAsDiscontinued() {
        return new RingVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.materials, this.gemstones, this.careInstructions, VariantStatusVO.DISCONTINUED);
    }
}
