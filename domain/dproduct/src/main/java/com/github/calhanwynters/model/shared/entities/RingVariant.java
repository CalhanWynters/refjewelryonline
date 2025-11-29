package com.github.calhanwynters.model.shared.entities;

import com.github.calhanwynters.model.ringattributes.RingSizeVO;
import com.github.calhanwynters.model.ringattributes.RingStyleVO;
import com.github.calhanwynters.model.shared.enums.VariantStatusEnums;
import com.github.calhanwynters.model.shared.valueobjects.CareInstructionVO;
import com.github.calhanwynters.model.shared.valueobjects.GemstoneVO;
import com.github.calhanwynters.model.shared.valueobjects.MaterialCompositionVO;
import com.github.calhanwynters.model.shared.valueobjects.PercentageVO;
import com.github.calhanwynters.model.shared.valueobjects.VariantId;

// Import JavaMoney interfaces
import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public record RingVariant(
        VariantId id,
        String sku,
        RingSizeVO size,
        RingStyleVO style,
        MonetaryAmount basePrice,
        MonetaryAmount currentPrice,
        Set<MaterialCompositionVO> materials,
        Set<GemstoneVO> gemstones,
        CareInstructionVO careInstructions,
        VariantStatusEnums status
) implements Variant {

    // Compact Constructor for Validation and Normalization
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

        if (materials.isEmpty()) {
            throw new IllegalArgumentException("Ring variant must have at least one material composition.");
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
    public static RingVariant create(
            RingSizeVO size,
            RingStyleVO style,
            MonetaryAmount basePrice,
            Set<MaterialCompositionVO> materials,
            CareInstructionVO careInstructions
    ) {
        VariantId generatedId = VariantId.generate();
        String generatedSku = "RING-" + generatedId.value().substring(0, 8).toUpperCase();

        return new RingVariant(
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
        if (!(other instanceof RingVariant otherRing)) {
            return false;
        }
        return Objects.equals(this.size, otherRing.size()) &&
                Objects.equals(this.style, otherRing.style()) &&
                Objects.equals(this.materials, otherRing.materials()) &&
                Objects.equals(this.gemstones, otherRing.gemstones()) &&
                Objects.equals(this.careInstructions, otherRing.careInstructions());
    }

    // --- Behavior Methods ---

    public RingVariant changeBasePrice(MonetaryAmount newBasePrice) {
        return new RingVariant(this.id, this.sku, this.size, this.style, newBasePrice, newBasePrice, this.materials, this.gemstones, this.careInstructions, this.status);
    }

    public RingVariant changeCurrentPrice(MonetaryAmount newCurrentPrice) {
        return new RingVariant(this.id, this.sku, this.size, this.style, this.basePrice, newCurrentPrice, this.materials, this.gemstones, this.careInstructions, this.status);
    }

    public RingVariant applyDiscount(PercentageVO discount) {
        MonetaryAmount discountedPrice = this.basePrice.multiply(BigDecimal.ONE.subtract(discount.value()));
        return this.changeCurrentPrice(discountedPrice);
    }

    public RingVariant removeDiscount() {
        return this.changeCurrentPrice(this.basePrice);
    }

    public RingVariant addGemstone(GemstoneVO gemstone) {
        Set<GemstoneVO> newGemstones = new HashSet<>(this.gemstones);
        newGemstones.add(gemstone);
        return new RingVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.materials, newGemstones, this.careInstructions, this.status);
    }

    public RingVariant removeGemstone(GemstoneVO gemstone) {
        Set<GemstoneVO> newGemstones = new HashSet<>(this.gemstones);
        if (newGemstones.remove(gemstone)) {
            return new RingVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.materials, newGemstones, this.careInstructions, this.status);
        }
        return this;
    }

    public RingVariant addMaterial(MaterialCompositionVO material) {
        Set<MaterialCompositionVO> newMaterials = new HashSet<>(this.materials);
        newMaterials.add(material);
        return new RingVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, newMaterials, this.gemstones, this.careInstructions, this.status);
    }

    public RingVariant removeMaterial(MaterialCompositionVO material) {
        Set<MaterialCompositionVO> newMaterials = new HashSet<>(this.materials);
        if (newMaterials.remove(material)) {
            if (newMaterials.isEmpty()) {
                throw new IllegalStateException("Ring variant must have at least one material composition; cannot remove the last one.");
            }
            return new RingVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, newMaterials, this.gemstones, this.careInstructions, this.status);
        }
        return this;
    }

    public RingVariant changeCareInstructions(CareInstructionVO newInstructions) {
        return new RingVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.materials, this.gemstones, newInstructions, this.status);
    }

    // --- Lifecycle/Status Behavior Methods ---

    public boolean isActive() {
        return this.status == VariantStatusEnums.ACTIVE;
    }

    public RingVariant activate() {
        if (this.status == VariantStatusEnums.DISCONTINUED) {
            throw new IllegalStateException("Cannot activate a discontinued variant.");
        }
        return new RingVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.materials, this.gemstones, this.careInstructions, VariantStatusEnums.ACTIVE);
    }

    public RingVariant deactivate() {
        return new RingVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.materials, this.gemstones, this.careInstructions, VariantStatusEnums.INACTIVE);
    }

    public RingVariant markAsDiscontinued() {
        return new RingVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.materials, this.gemstones, this.careInstructions, VariantStatusEnums.DISCONTINUED);
    }
}
