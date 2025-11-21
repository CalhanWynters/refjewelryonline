package com.github.calhanwynters.model.shared.entities;

import com.github.calhanwynters.model.earringattributes.EarringSizeVO;
import com.github.calhanwynters.model.earringattributes.EarringStyleVO;
import com.github.calhanwynters.model.shared.enums.VariantStatusVO;
import com.github.calhanwynters.model.shared.valueobjects.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public record EarringVariant(
        VariantId id,
        String sku, // Added SKU field based on previous discussion
        EarringSizeVO size,
        EarringStyleVO style,
        PriceVO basePrice,     // Changed 'price' to 'basePrice' for consistency
        PriceVO currentPrice,  // Added 'currentPrice' for consistency
        Set<MaterialCompositionVO> materials,
        Set<GemstoneVO> gemstones,
        CareInstructionVO careInstructions,
        VariantStatusVO status // Added status field for consistency
) implements Variant {

    public EarringVariant {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(sku, "sku must not be null"); // Validate SKU
        Objects.requireNonNull(size, "size must not be null");
        Objects.requireNonNull(style, "style must not be null");
        Objects.requireNonNull(basePrice, "basePrice must not be null");
        Objects.requireNonNull(currentPrice, "currentPrice must not be null");
        Objects.requireNonNull(materials, "materials must not be null");
        Objects.requireNonNull(gemstones, "gemstones must not be null");
        Objects.requireNonNull(careInstructions, "careInstructions must not be null");
        Objects.requireNonNull(status, "status must not be null"); // Validate status

        // Enforce business invariant: variant must have at least one material
        if (materials.isEmpty()) {
            throw new IllegalArgumentException("Earring variant must have at least one material composition.");
        }

        // Use Set.copyOf() in the compact constructor for immutability
        materials = Set.copyOf(materials);
        gemstones = Set.copyOf(gemstones);
    }

    public static EarringVariant create(
            EarringSizeVO size,
            EarringStyleVO style,
            PriceVO basePrice, // Use basePrice in create method
            Set<MaterialCompositionVO> materials,
            CareInstructionVO careInstructions
    ) {
        VariantId generatedId = VariantId.generate();
        String generatedSku = "ERRNG-" + generatedId.value().substring(0, 8).toUpperCase(); // Generate SKU

        // We use Set.of() for an empty immutable set of gemstones by default
        return new EarringVariant(
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

    // --- New Behavior: Variant Comparison Logic ---

    @Override
    public boolean hasSameAttributes(Variant other) {
        if (!(other instanceof EarringVariant otherEarring)) {
            return false; // Not the same *type* of variant
        }
        // Compare all significant physical/descriptive attributes
        return Objects.equals(this.size, otherEarring.size()) &&
                Objects.equals(this.style, otherEarring.style()) &&
                Objects.equals(this.materials, otherEarring.materials()) &&
                Objects.equals(this.gemstones, otherEarring.gemstones()) &&
                Objects.equals(this.careInstructions, otherEarring.careInstructions());
    }

    // --- Behavior Methods ---

    // Renamed changePrice to changeBasePrice for consistency, applying price invariant
    public EarringVariant changeBasePrice(PriceVO newBasePrice) {
        // Enforce the invariant: When base price changes, current price must match it.
        return new EarringVariant(this.id, this.sku, this.size, this.style, newBasePrice, newBasePrice, this.materials, this.gemstones, this.careInstructions, this.status);
    }

    // Added changeCurrentPrice for manual adjustments/discounts
    public EarringVariant changeCurrentPrice(PriceVO newCurrentPrice) {
        return new EarringVariant(this.id, this.sku, this.size, this.style, this.basePrice, newCurrentPrice, this.materials, this.gemstones, this.careInstructions, this.status);
    }

    /**
     * Applies a percentage discount to the current price.
     */
    public EarringVariant applyDiscount(PercentageVO discount) {
        BigDecimal discountedAmount = this.basePrice.amount().multiply(BigDecimal.ONE.subtract(discount.value()));
        PriceVO discountedPrice = new PriceVO(discountedAmount, this.basePrice.currency());
        return this.changeCurrentPrice(discountedPrice);
    }

    /**
     * Removes the applied discount and reverts the price to the base price.
     */
    public EarringVariant removeDiscount() {
        return this.changeCurrentPrice(this.basePrice);
    }

    public EarringVariant addGemstone(GemstoneVO gemstone) {
        Set<GemstoneVO> newGemstones = new HashSet<>(this.gemstones);
        newGemstones.add(gemstone);
        return new EarringVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.materials, newGemstones, this.careInstructions, this.status);
    }

    /**
     * Removes a gemstone from the variant.
     * @param gemstone The gemstone to remove.
     * @return A new EarringVariant instance without the specified gemstone.
     */
    public EarringVariant removeGemstone(GemstoneVO gemstone) {
        Set<GemstoneVO> newGemstones = new HashSet<>(this.gemstones);
        if (newGemstones.remove(gemstone)) {
            return new EarringVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.materials, newGemstones, this.careInstructions, this.status);
        }
        return this; // Return the current instance if no change occurred
    }

    /**
     * Adds a material composition.
     */
    public EarringVariant addMaterial(MaterialCompositionVO material) {
        Set<MaterialCompositionVO> newMaterials = new HashSet<>(this.materials);
        newMaterials.add(material);
        return new EarringVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, newMaterials, this.gemstones, this.careInstructions, this.status);
    }

    /**
     * Removes a material composition.
     * @param material The material to remove.
     * @return A new EarringVariant instance without the specified material.
     */
    public EarringVariant removeMaterial(MaterialCompositionVO material) {
        Set<MaterialCompositionVO> newMaterials = new HashSet<>(this.materials);
        if (newMaterials.remove(material)) {
            // Must re-validate that the resulting set is not empty before creating the new instance
            if (newMaterials.isEmpty()) {
                throw new IllegalStateException("Earring variant must have at least one material composition; cannot remove the last one.");
            }
            return new EarringVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, newMaterials, this.gemstones, this.careInstructions, this.status);
        }
        return this; // Return the current instance if no change occurred
    }

    /**
     * Updates the care instructions.
     */
    public EarringVariant changeCareInstructions(CareInstructionVO newInstructions) {
        return new EarringVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.materials, this.gemstones, newInstructions, this.status);
    }

    // --- Lifecycle/Status Behavior Methods (added for consistency with AnkletVariant) ---

    public boolean isActive() {
        return this.status == VariantStatusVO.ACTIVE;
    }

    public EarringVariant activate() {
        if (this.status == VariantStatusVO.DISCONTINUED) {
            throw new IllegalStateException("Cannot activate a discontinued variant.");
        }
        return new EarringVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.materials, this.gemstones, this.careInstructions, VariantStatusVO.ACTIVE);
    }

    public EarringVariant deactivate() {
        return new EarringVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.materials, this.gemstones, this.careInstructions, VariantStatusVO.INACTIVE);
    }

    public EarringVariant markAsDiscontinued() {
        return new EarringVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.materials, this.gemstones, this.careInstructions, VariantStatusVO.DISCONTINUED);
    }
}
