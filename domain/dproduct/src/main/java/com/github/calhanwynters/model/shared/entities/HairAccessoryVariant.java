package com.github.calhanwynters.model.shared.entities;

import com.github.calhanwynters.model.hairaccessoryattributes.HairAccessorySizeVO;
import com.github.calhanwynters.model.hairaccessoryattributes.HairAccessorStyleVO;
import com.github.calhanwynters.model.shared.enums.VariantStatusEnums;
import com.github.calhanwynters.model.shared.valueobjects.*;

// Import JavaMoney interfaces
import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public record HairAccessoryVariant(
        VariantId id,
        String sku,
        HairAccessorySizeVO size,
        HairAccessorStyleVO style,
        MonetaryAmount basePrice,
        MonetaryAmount currentPrice,
        Set<MaterialCompositionVO> materials,
        Set<GemstoneVO> gemstones,
        CareInstructionVO careInstructions,
        VariantStatusEnums status
) implements Variant {

    // Compact Constructor for Validation and Normalization
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

        if (materials.isEmpty()) {
            throw new IllegalArgumentException("Hair accessory variant must have at least one material composition.");
        }

        materials = Set.copyOf(materials);
        gemstones = Set.copyOf(gemstones);

        if (!basePrice.getCurrency().equals(currentPrice.getCurrency())) {
            throw new IllegalArgumentException("Base price and current price must be in the same currency.");
        }
    }

    /**
     * Factory method to create a new DRAFT variant.
     */
    public static HairAccessoryVariant create(
            HairAccessorySizeVO size,
            HairAccessorStyleVO style,
            MonetaryAmount basePrice,
            Set<MaterialCompositionVO> materials,
            CareInstructionVO careInstructions
    ) {
        VariantId generatedId = VariantId.generate();
        String generatedSku = "HAIRACC-" + generatedId.value().substring(0, 8).toUpperCase();

        return new HairAccessoryVariant(
                generatedId,
                generatedSku,
                size,
                style,
                basePrice,
                basePrice, // current price starts the same as base price
                materials,
                Set.of(), // No gemstones initially
                careInstructions,
                VariantStatusEnums.DRAFT
        );
    }

    // --- Variant Comparison Logic ---

    @Override
    public boolean hasSameAttributes(Variant other) {
        if (!(other instanceof HairAccessoryVariant otherHairAccessory)) {
            return false;
        }
        return Objects.equals(this.size, otherHairAccessory.size()) &&
                Objects.equals(this.style, otherHairAccessory.style()) &&
                Objects.equals(this.materials, otherHairAccessory.materials()) &&
                Objects.equals(this.gemstones, otherHairAccessory.gemstones()) &&
                Objects.equals(this.careInstructions, otherHairAccessory.careInstructions());
    }

    // --- Behavior Methods ---

    public HairAccessoryVariant changeBasePrice(MonetaryAmount newBasePrice) {
        return new HairAccessoryVariant(this.id, this.sku, this.size, this.style, newBasePrice, newBasePrice, this.materials, this.gemstones, this.careInstructions, this.status);
    }

    public HairAccessoryVariant changeCurrentPrice(MonetaryAmount newCurrentPrice) {
        return new HairAccessoryVariant(this.id, this.sku, this.size, this.style, this.basePrice, newCurrentPrice, this.materials, this.gemstones, this.careInstructions, this.status);
    }

    public HairAccessoryVariant applyDiscount(PercentageVO discount) {
        MonetaryAmount discountedPrice = this.basePrice.multiply(BigDecimal.ONE.subtract(discount.value()));
        return this.changeCurrentPrice(discountedPrice);
    }

    public HairAccessoryVariant removeDiscount() {
        return this.changeCurrentPrice(this.basePrice);
    }

    public HairAccessoryVariant addGemstone(GemstoneVO gemstone) {
        Set<GemstoneVO> newGemstones = new HashSet<>(this.gemstones);
        newGemstones.add(gemstone);
        return new HairAccessoryVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.materials, newGemstones, this.careInstructions, this.status);
    }

    public HairAccessoryVariant removeGemstone(GemstoneVO gemstone) {
        Set<GemstoneVO> newGemstones = new HashSet<>(this.gemstones);
        if (newGemstones.remove(gemstone)) {
            return new HairAccessoryVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.materials, newGemstones, this.careInstructions, this.status);
        }
        return this;
    }

    public HairAccessoryVariant addMaterial(MaterialCompositionVO material) {
        Set<MaterialCompositionVO> newMaterials = new HashSet<>(this.materials);
        newMaterials.add(material);
        return new HairAccessoryVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, newMaterials, this.gemstones, this.careInstructions, this.status);
    }

    public HairAccessoryVariant removeMaterial(MaterialCompositionVO material) {
        Set<MaterialCompositionVO> newMaterials = new HashSet<>(this.materials);
        if (newMaterials.remove(material)) {
            if (newMaterials.isEmpty()) {
                throw new IllegalStateException("Hair accessory variant must have at least one material composition; cannot remove the last one.");
            }
            return new HairAccessoryVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, newMaterials, this.gemstones, this.careInstructions, this.status);
        }
        return this;
    }

    public HairAccessoryVariant changeCareInstructions(CareInstructionVO newInstructions) {
        return new HairAccessoryVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.materials, this.gemstones, newInstructions, this.status);
    }

    // --- Lifecycle/Status Behavior Methods ---

    public boolean isActive() {
        return this.status == VariantStatusEnums.ACTIVE;
    }

    public HairAccessoryVariant activate() {
        if (this.status == VariantStatusEnums.DISCONTINUED) {
            throw new IllegalStateException("Cannot activate a discontinued variant.");
        }
        return new HairAccessoryVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.materials, this.gemstones, this.careInstructions, VariantStatusEnums.ACTIVE);
    }

    public HairAccessoryVariant deactivate() {
        return new HairAccessoryVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.materials, this.gemstones, this.careInstructions, VariantStatusEnums.INACTIVE);
    }

    public HairAccessoryVariant markAsDiscontinued() {
        return new HairAccessoryVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.materials, this.gemstones, this.careInstructions, VariantStatusEnums.DISCONTINUED);
    }
}
