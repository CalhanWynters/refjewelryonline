package com.github.calhanwynters.model.shared.entities;

import com.github.calhanwynters.model.necklaceattributes.NecklaceSizeVO;
import com.github.calhanwynters.model.necklaceattributes.NecklaceStyleVO;
import com.github.calhanwynters.model.shared.enums.VariantStatusEnums;
import com.github.calhanwynters.model.shared.valueobjects.*;

// Import JavaMoney interfaces
import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public record NecklaceVariant(
        VariantId id,
        String sku,
        NecklaceSizeVO size,
        NecklaceStyleVO style,
        MonetaryAmount basePrice,
        MonetaryAmount currentPrice,
        WeightVO weight,  // Added weight property
        Set<MaterialCompositionVO> materials,
        Set<GemstoneVO> gemstones,
        CareInstructionVO careInstructions,
        VariantStatusEnums status
) implements Variant {

    // Compact Constructor for Validation and Normalization
    public NecklaceVariant {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(sku, "sku must not be null");
        Objects.requireNonNull(size, "size must not be null");
        Objects.requireNonNull(style, "style must not be null");
        Objects.requireNonNull(basePrice, "basePrice must not be null");
        Objects.requireNonNull(currentPrice, "currentPrice must not be null");
        Objects.requireNonNull(weight, "weight must not be null");  // Validate weight
        Objects.requireNonNull(materials, "materials must not be null");
        Objects.requireNonNull(gemstones, "gemstones must not be null");
        Objects.requireNonNull(careInstructions, "careInstructions must not be null");
        Objects.requireNonNull(status, "status must not be null");

        if (materials.isEmpty()) {
            throw new IllegalArgumentException("Necklace variant must have at least one material composition.");
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
    public static NecklaceVariant create(
            NecklaceSizeVO size,
            NecklaceStyleVO style,
            MonetaryAmount basePrice,
            WeightVO weight,  // Added weight parameter
            Set<MaterialCompositionVO> materials,
            CareInstructionVO careInstructions
    ) {
        VariantId generatedId = VariantId.generate();
        String generatedSku = "NKLACE-" + generatedId.value().substring(0, 8).toUpperCase();

        return new NecklaceVariant(
                generatedId,
                generatedSku,
                size,
                style,
                basePrice,
                basePrice, // current price starts the same as base price
                weight,  // Set weight
                materials,
                Set.of(), // No gemstones initially
                careInstructions,
                VariantStatusEnums.DRAFT
        );
    }

    // --- Variant Comparison Logic ---

    @Override
    public boolean hasSameAttributes(Variant other) {
        if (!(other instanceof NecklaceVariant otherNecklace)) {
            return false;
        }
        return Objects.equals(this.size, otherNecklace.size()) &&
                Objects.equals(this.style, otherNecklace.style()) &&
                Objects.equals(this.materials, otherNecklace.materials()) &&
                Objects.equals(this.gemstones, otherNecklace.gemstones()) &&
                Objects.equals(this.careInstructions, otherNecklace.careInstructions()) &&
                Objects.equals(this.weight, otherNecklace.weight());  // Include weight in comparison
    }

    // --- Behavior Methods ---

    public NecklaceVariant changeBasePrice(MonetaryAmount newBasePrice) {
        return new NecklaceVariant(this.id, this.sku, this.size, this.style, newBasePrice, newBasePrice, this.weight, this.materials, this.gemstones, this.careInstructions, this.status);
    }

    public NecklaceVariant changeCurrentPrice(MonetaryAmount newCurrentPrice) {
        return new NecklaceVariant(this.id, this.sku, this.size, this.style, this.basePrice, newCurrentPrice, this.weight, this.materials, this.gemstones, this.careInstructions, this.status);
    }

    public NecklaceVariant applyDiscount(PercentageVO discount) {
        MonetaryAmount discountedPrice = this.basePrice.multiply(BigDecimal.ONE.subtract(discount.value()));
        return this.changeCurrentPrice(discountedPrice);
    }

    public NecklaceVariant removeDiscount() {
        return this.changeCurrentPrice(this.basePrice);
    }

    public NecklaceVariant addGemstone(GemstoneVO gemstone) {
        Set<GemstoneVO> newGemstones = new HashSet<>(this.gemstones);
        newGemstones.add(gemstone);
        return new NecklaceVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.weight, this.materials, newGemstones, this.careInstructions, this.status);
    }

    public NecklaceVariant removeGemstone(GemstoneVO gemstone) {
        Set<GemstoneVO> newGemstones = new HashSet<>(this.gemstones);
        if (newGemstones.remove(gemstone)) {
            return new NecklaceVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.weight, this.materials, newGemstones, this.careInstructions, this.status);
        }
        return this;
    }

    public NecklaceVariant addMaterial(MaterialCompositionVO material) {
        Set<MaterialCompositionVO> newMaterials = new HashSet<>(this.materials);
        newMaterials.add(material);
        return new NecklaceVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.weight, newMaterials, this.gemstones, this.careInstructions, this.status);
    }

    public NecklaceVariant removeMaterial(MaterialCompositionVO material) {
        Set<MaterialCompositionVO> newMaterials = new HashSet<>(this.materials);
        if (newMaterials.remove(material)) {
            if (newMaterials.isEmpty()) {
                throw new IllegalStateException("Necklace variant must have at least one material composition; cannot remove the last one.");
            }
            return new NecklaceVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.weight, newMaterials, this.gemstones, this.careInstructions, this.status);
        }
        return this;
    }

    public NecklaceVariant changeCareInstructions(CareInstructionVO newInstructions) {
        return new NecklaceVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.weight, this.materials, this.gemstones, newInstructions, this.status);
    }

    // --- Lifecycle/Status Behavior Methods ---

    public boolean isActive() {
        return this.status == VariantStatusEnums.ACTIVE;
    }

    public NecklaceVariant activate() {
        if (this.status == VariantStatusEnums.DISCONTINUED) {
            throw new IllegalStateException("Cannot activate a discontinued variant.");
        }
        return new NecklaceVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.weight, this.materials, this.gemstones, this.careInstructions, VariantStatusEnums.ACTIVE);
    }

    public NecklaceVariant deactivate() {
        return new NecklaceVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.weight, this.materials, this.gemstones, this.careInstructions, VariantStatusEnums.INACTIVE);
    }

    public NecklaceVariant markAsDiscontinued() {
        return new NecklaceVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.weight, this.materials, this.gemstones, this.careInstructions, VariantStatusEnums.DISCONTINUED);
    }
}

