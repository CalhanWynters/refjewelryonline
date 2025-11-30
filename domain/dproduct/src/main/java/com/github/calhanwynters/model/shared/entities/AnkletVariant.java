package com.github.calhanwynters.model.shared.entities;

import com.github.calhanwynters.model.ankletattributes.AnkletSizeVO;
import com.github.calhanwynters.model.ankletattributes.AnkletStyleVO;
import com.github.calhanwynters.model.shared.enums.VariantStatusEnums;
import com.github.calhanwynters.model.shared.valueobjects.*;

// Import JavaMoney interfaces
import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public record AnkletVariant(
        VariantId id,
        String sku,
        AnkletSizeVO size,
        AnkletStyleVO style,
        MonetaryAmount basePrice,
        MonetaryAmount currentPrice,
        WeightVO weight,  // Added weight property
        Set<MaterialCompositionVO> materials,
        Set<GemstoneVO> gemstones,
        CareInstructionVO careInstructions,
        VariantStatusEnums status
) implements Variant {

    public AnkletVariant {
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
            throw new IllegalArgumentException("Anklet variant must have at least one material composition.");
        }

        materials = Set.copyOf(materials);
        gemstones = Set.copyOf(gemstones);

        if (!basePrice.getCurrency().equals(currentPrice.getCurrency())) {
            throw new IllegalArgumentException("Base price and current price must be in the same currency.");
        }
    }

    public static AnkletVariant create(
            AnkletSizeVO size,
            AnkletStyleVO style,
            MonetaryAmount basePrice,
            WeightVO weight,  // Added weight parameter
            Set<MaterialCompositionVO> materials,
            CareInstructionVO careInstructions
    ) {
        VariantId generatedId = VariantId.generate();
        String generatedSku = "ANKLT-" + generatedId.value().substring(0, 8).toUpperCase();

        return new AnkletVariant(
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

    @Override
    public boolean hasSameAttributes(Variant other) {
        if (!(other instanceof AnkletVariant otherAnklet)) {
            return false;
        }
        return Objects.equals(this.size, otherAnklet.size()) &&
                Objects.equals(this.style, otherAnklet.style()) &&
                Objects.equals(this.weight, otherAnklet.weight()) &&  // Include weight in comparison
                Objects.equals(this.materials, otherAnklet.materials()) &&
                Objects.equals(this.gemstones, otherAnklet.gemstones()) &&
                Objects.equals(this.careInstructions, otherAnklet.careInstructions());
    }

    public AnkletVariant changeBasePrice(MonetaryAmount newBasePrice) {
        return new AnkletVariant(this.id, this.sku, this.size, this.style, newBasePrice, newBasePrice, this.weight, this.materials, this.gemstones, this.careInstructions, this.status);
    }

    public AnkletVariant changeCurrentPrice(MonetaryAmount newCurrentPrice) {
        return new AnkletVariant(this.id, this.sku, this.size, this.style, this.basePrice, newCurrentPrice, this.weight, this.materials, this.gemstones, this.careInstructions, this.status);
    }

    public AnkletVariant applyDiscount(PercentageVO discount) {
        MonetaryAmount discountedPrice = this.basePrice.multiply(BigDecimal.ONE.subtract(discount.value()));
        return this.changeCurrentPrice(discountedPrice);
    }

    public AnkletVariant removeDiscount() {
        return this.changeCurrentPrice(this.basePrice);
    }

    public AnkletVariant addGemstone(GemstoneVO gemstone) {
        Set<GemstoneVO> newGemstones = new HashSet<>(this.gemstones);
        newGemstones.add(gemstone);
        return new AnkletVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.weight, this.materials, newGemstones, this.careInstructions, this.status);
    }

    public AnkletVariant removeGemstone(GemstoneVO gemstone) {
        Set<GemstoneVO> newGemstones = new HashSet<>(this.gemstones);
        if (newGemstones.remove(gemstone)) {
            return new AnkletVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.weight, this.materials, newGemstones, this.careInstructions, this.status);
        }
        return this;
    }

    public AnkletVariant addMaterial(MaterialCompositionVO material) {
        Set<MaterialCompositionVO> newMaterials = new HashSet<>(this.materials);
        newMaterials.add(material);
        return new AnkletVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.weight, newMaterials, this.gemstones, this.careInstructions, this.status);
    }

    public AnkletVariant removeMaterial(MaterialCompositionVO material) {
        Set<MaterialCompositionVO> newMaterials = new HashSet<>(this.materials);
        if (newMaterials.remove(material)) {
            if (newMaterials.isEmpty()) {
                throw new IllegalStateException("Anklet variant must have at least one material composition; cannot remove the last one.");
            }
            return new AnkletVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.weight, newMaterials, this.gemstones, this.careInstructions, this.status);
        }
        return this;
    }

    public AnkletVariant changeCareInstructions(CareInstructionVO newInstructions) {
        return new AnkletVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.weight, this.materials, this.gemstones, newInstructions, this.status);
    }

    // --- Lifecycle/Status Behavior Methods ---

    public boolean isActive() {
        return this.status == VariantStatusEnums.ACTIVE;
    }

    public AnkletVariant activate() {
        if (this.status == VariantStatusEnums.DISCONTINUED) {
            throw new IllegalStateException("Cannot activate a discontinued variant.");
        }
        return new AnkletVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.weight, this.materials, this.gemstones, this.careInstructions, VariantStatusEnums.ACTIVE);
    }

    public AnkletVariant deactivate() {
        return new AnkletVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.weight, this.materials, this.gemstones, this.careInstructions, VariantStatusEnums.INACTIVE);
    }

    public AnkletVariant markAsDiscontinued() {
        return new AnkletVariant(this.id, this.sku, this.size, this.style, this.basePrice, this.currentPrice, this.weight, this.materials, this.gemstones, this.careInstructions, VariantStatusEnums.DISCONTINUED);
    }
}


