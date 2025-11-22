package com.github.calhanwynters.model.shared.entities;

import com.github.calhanwynters.model.necklaceattributes.NecklaceSizeVO;
import com.github.calhanwynters.model.necklaceattributes.NecklaceStyleVO;
import com.github.calhanwynters.model.shared.enums.VariantStatusVO;
import com.github.calhanwynters.model.shared.valueobjects.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public record NecklaceVariant(
        VariantId id, // Using the shared VariantId from shared.valueobjects
        String sku, // Added SKU field
        NecklaceSizeVO size, // Specific VO
        NecklaceStyleVO style, // Specific VO
        PriceVO basePrice, // Changed 'price' to 'basePrice'
        PriceVO currentPrice, // Added 'currentPrice'
        Set<MaterialCompositionVO> materials,
        Set<GemstoneVO> gemstones,
        CareInstructionVO careInstructions,
        VariantStatusVO status // Added status field
) implements Variant {

    public NecklaceVariant {
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
            throw new IllegalArgumentException("Necklace variant must have at least one material composition.");
        }

        // Use Set.copyOf() in the compact constructor for immutability
        materials = Set.copyOf(materials);
        gemstones = Set.copyOf(gemstones);
    }

    public static NecklaceVariant create(
            NecklaceSizeVO size,
            NecklaceStyleVO style,
            PriceVO basePrice, // Use basePrice in create method
            Set<MaterialCompositionVO> materials,
            CareInstructionVO careInstructions
    ) {
        VariantId generatedId = VariantId.generate();
        String generatedSku = "NCLCE-" + generatedId.value().substring(0, 8).toUpperCase(); // Generate SKU

        // We use the shared VariantId.generate() here
        return new NecklaceVariant(
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
        if (!(other instanceof NecklaceVariant otherNecklace)) {
            return false; // Not the same *type* of variant
        }
        // Compare all significant physical/descriptive attributes
        return Objects.equals(this.size, otherNecklace.size()) &&
                Objects.equals(this.style, otherNecklace.style()) &&
                Objects.equals(this.materials, otherNecklace.materials()) &&
                Objects.equals(this.gemstones, otherNecklace.gemstones()) &&
                Objects.equals(this.careInstructions, otherNecklace.careInstructions());
    }

    // --- Behavior Methods ---

    public NecklaceVariant changeBasePrice(PriceVO newBasePrice) {
        // Enforce the invariant: When base price changes, current price must match it.
        return new NecklaceVariant(this.id, this.sku, this.size, this.style, newBasePrice, newBasePrice, this.materials, this.gemstones, this.careInstructions, this.status);
    }

    public NecklaceVariant changeCurrentPrice(PriceVO newCurrentPrice) {
        // This method is used for manual price adjustments or discounts.
        return new NecklaceVariant(this.id, this.sku, this.size, this.style, this.basePrice, newCurrentPrice, this.materials, this.gemstones, this.careInstructions, this.status);
    }

    /**
     * Applies a percentage discount to the current price.
     */
    public NecklaceVariant applyDiscount(PercentageVO discount) {
        BigDecimal discountedAmount = this.basePrice.amount().multiply(BigDecimal.ONE.subtract(discount.value()));
        PriceVO discountedPrice = new PriceVO(discountedAmount, this.basePrice.currency());
        return this.changeCurrentPrice(discountedPrice);
    }

    /**
     * Removes the applied discount and reverts the price to the base price.
     */
    public NecklaceVariant removeDiscount() {
        return this.changeCurrentPrice(this.basePrice);
    }

    public NecklaceVariant addGemstone(GemstoneVO gemstone) {
        Set<GemstoneVO> newGemstones = new HashSet<>(this.gemstones);
        newGemstones.add(gemstone);
        return new NecklaceVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.materials, newGemstones, this.careInstructions, this.status);
    }

    /**
     * Removes a gemstone from the variant.
     */
    public NecklaceVariant removeGemstone(GemstoneVO gemstone) {
        Set<GemstoneVO> newGemstones = new HashSet<>(this.gemstones);
        if (newGemstones.remove(gemstone)) {
            return new NecklaceVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.materials, newGemstones, this.careInstructions, this.status);
        }
        return this;
    }

    /**
     * Adds a material composition.
     */
    public NecklaceVariant addMaterial(MaterialCompositionVO material) {
        Set<MaterialCompositionVO> newMaterials = new HashSet<>(this.materials);
        newMaterials.add(material);
        return new NecklaceVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, newMaterials, this.gemstones, this.careInstructions, this.status);
    }

    /**
     * Removes a material composition.
     */
    public NecklaceVariant removeMaterial(MaterialCompositionVO material) {
        Set<MaterialCompositionVO> newMaterials = new HashSet<>(this.materials);
        if (newMaterials.remove(material)) {
            // Must re-validate that the resulting set is not empty before creating the new instance
            if (newMaterials.isEmpty()) {
                throw new IllegalStateException("Necklace variant must have at least one material composition; cannot remove the last one.");
            }
            return new NecklaceVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, newMaterials, this.gemstones, this.careInstructions, this.status);
        }
        return this;
    }

    /**
     * Updates the care instructions.
     */
    public NecklaceVariant changeCareInstructions(CareInstructionVO newInstructions) {
        return new NecklaceVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.materials, this.gemstones, newInstructions, this.status);
    }

    // --- Lifecycle/Status Behavior Methods (added for consistency) ---

    public boolean isActive() {
        return this.status == VariantStatusVO.ACTIVE;
    }

    public NecklaceVariant activate() {
        if (this.status == VariantStatusVO.DISCONTINUED) {
            throw new IllegalStateException("Cannot activate a discontinued variant.");
        }
        return new NecklaceVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.materials, this.gemstones, this.careInstructions, VariantStatusVO.ACTIVE);
    }

    public NecklaceVariant deactivate() {
        return new NecklaceVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.materials, this.gemstones, this.careInstructions, VariantStatusVO.INACTIVE);
    }

    public NecklaceVariant markAsDiscontinued() {
        return new NecklaceVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.materials, this.gemstones, this.careInstructions, VariantStatusVO.DISCONTINUED);
    }
}
