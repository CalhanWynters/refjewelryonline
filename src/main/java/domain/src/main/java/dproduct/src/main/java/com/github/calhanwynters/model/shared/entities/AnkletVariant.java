package com.github.calhanwynters.model.shared.entities;

import com.github.calhanwynters.model.ankletattributes.AnkletSizeVO;
import com.github.calhanwynters.model.ankletattributes.AnkletStyleVO;
import com.github.calhanwynters.model.shared.enums.VariantStatusVO;
import com.github.calhanwynters.model.shared.valueobjects.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public record AnkletVariant(
        VariantId id,
        String sku, // <-- New SKU field
        AnkletSizeVO size,
        AnkletStyleVO style,
        PriceVO basePrice,
        PriceVO currentPrice,
        Set<MaterialCompositionVO> materials,
        Set<GemstoneVO> gemstones,
        CareInstructionVO careInstructions,
        VariantStatusVO status
) implements Variant {

    public AnkletVariant {
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

        if (materials.isEmpty()) {
            throw new IllegalArgumentException("Anklet variant must have at least one material composition.");
        }

        materials = Set.copyOf(materials);
        gemstones = Set.copyOf(gemstones);
    }

    public static AnkletVariant create(
            AnkletSizeVO size,
            AnkletStyleVO style,
            PriceVO basePrice,
            Set<MaterialCompositionVO> materials,
            CareInstructionVO careInstructions
    ) {
        VariantId generatedId = VariantId.generate();
        // A simple example of generating a standard SKU based on the type and ID
        String generatedSku = "ANKLT-" + generatedId.value().substring(0, 8).toUpperCase();

        return new AnkletVariant(
                generatedId,
                generatedSku, // Pass the generated SKU to the constructor
                size,
                style,
                basePrice,
                basePrice,
                materials,
                Set.of(),
                careInstructions,
                VariantStatusVO.DRAFT
        );
    }

    // --- New Behavior: Variant Comparison Logic ---

    @Override
    public boolean hasSameAttributes(Variant other) {
        if (!(other instanceof AnkletVariant otherAnklet)) {
            return false; // Not the same *type* of variant
        }
        // Compare all significant physical/descriptive attributes
        return Objects.equals(this.size, otherAnklet.size()) &&
                Objects.equals(this.style, otherAnklet.style()) &&
                Objects.equals(this.materials, otherAnklet.materials()) &&
                Objects.equals(this.gemstones, otherAnklet.gemstones()) &&
                Objects.equals(this.careInstructions, otherAnklet.careInstructions());
    }

    // --- Behavior Methods ---

    // Note: The record accessors id(), sku(), basePrice(), currentPrice(), status(), etc.
    // are generated automatically and fulfill the interface contract.

    public AnkletVariant changeBasePrice(PriceVO newBasePrice) {
        // Enforce the invariant: When base price changes, current price must match it.
        // This ensures the current price is always "correct" relative to the new base price, clearing any old discounts/manual changes.
        return new AnkletVariant(this.id, this.sku, this.size, this.style, newBasePrice, newBasePrice, this.materials, this.gemstones, this.careInstructions, this.status);
    }

    public AnkletVariant changeCurrentPrice(PriceVO newCurrentPrice) {
        // This method is used for manual price adjustments or discounts, which override the base price temporarily.
        // It maintains the integrity of the *current* pricing state.
        return new AnkletVariant(this.id, this.sku, this.size, this.style, this.basePrice, newCurrentPrice, this.materials, this.gemstones, this.careInstructions, this.status);
    }

    /**
     * Applies a percentage discount to the current price.
     */
    public AnkletVariant applyDiscount(PercentageVO discount) {
        BigDecimal discountedAmount = this.basePrice.amount().multiply(BigDecimal.ONE.subtract(discount.value()));
        // Ensure discounted amount doesn't go negative, handled implicitly if PriceVO validates non-negative amounts
        PriceVO discountedPrice = new PriceVO(discountedAmount, this.basePrice.currency());
        return this.changeCurrentPrice(discountedPrice);
    }

    /**
     * Removes the applied discount and reverts the price to the base price.
     */
    public AnkletVariant removeDiscount() {
        return this.changeCurrentPrice(this.basePrice);
    }

    public AnkletVariant addGemstone(GemstoneVO gemstone) {
        Set<GemstoneVO> newGemstones = new HashSet<>(this.gemstones);
        newGemstones.add(gemstone);
        return new AnkletVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.materials, newGemstones, this.careInstructions, this.status);
    }

    public AnkletVariant removeGemstone(GemstoneVO gemstone) {
        Set<GemstoneVO> newGemstones = new HashSet<>(this.gemstones);
        if (newGemstones.remove(gemstone)) {
            return new AnkletVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.materials, newGemstones, this.careInstructions, this.status);
        }
        return this;
    }

    public AnkletVariant addMaterial(MaterialCompositionVO material) {
        Set<MaterialCompositionVO> newMaterials = new HashSet<>(this.materials);
        newMaterials.add(material);
        // The constructor validation for non-empty materials ensures this is safe as long as the original set wasn't empty
        return new AnkletVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, newMaterials, this.gemstones, this.careInstructions, this.status);
    }

    public AnkletVariant removeMaterial(MaterialCompositionVO material) {
        Set<MaterialCompositionVO> newMaterials = new HashSet<>(this.materials);
        if (newMaterials.remove(material)) {
            // Must re-validate that the resulting set is not empty before creating the new instance
            if (newMaterials.isEmpty()) {
                throw new IllegalStateException("Anklet variant must have at least one material composition; cannot remove the last one.");
            }
            return new AnkletVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, newMaterials, this.gemstones, this.careInstructions, this.status);
        }
        return this;
    }

    public AnkletVariant changeCareInstructions(CareInstructionVO newInstructions) {
        return new AnkletVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.materials, this.gemstones, newInstructions, this.status);
    }

    // --- Lifecycle/Status Behavior Methods ---

    public boolean isActive() {
        return this.status == VariantStatusVO.ACTIVE;
    }

    public AnkletVariant activate() {
        if (this.status == VariantStatusVO.DISCONTINUED) {
            throw new IllegalStateException("Cannot activate a discontinued variant.");
        }
        return new AnkletVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.materials, this.gemstones, this.careInstructions, VariantStatusVO.ACTIVE);
    }

    public AnkletVariant deactivate() {
        return new AnkletVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.materials, this.gemstones, this.careInstructions, VariantStatusVO.INACTIVE);
    }

    public AnkletVariant markAsDiscontinued() {
        return new AnkletVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.materials, this.gemstones, this.careInstructions, VariantStatusVO.DISCONTINUED);
    }
}
