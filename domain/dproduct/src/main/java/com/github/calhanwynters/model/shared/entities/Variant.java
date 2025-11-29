package com.github.calhanwynters.model.shared.entities;

import com.github.calhanwynters.model.shared.enums.VariantStatusEnums;
import com.github.calhanwynters.model.shared.valueobjects.*;

import javax.money.MonetaryAmount; // Import the standard API interface
import java.util.Set;

public interface Variant {
    VariantId id();
    String sku();

    MonetaryAmount basePrice();

    MonetaryAmount currentPrice();

    CareInstructionVO careInstructions();
    Set<MaterialCompositionVO> materials();
    Set<GemstoneVO> gemstones();
    VariantStatusEnums status();

    /**
     * Checks if this variant has the same physical attributes as another variant,
     * ignoring identity (ID, SKU) and volatile properties (price, status).
     * This is useful for ensuring uniqueness within a Product aggregate.
     */
    boolean hasSameAttributes(Variant other);
}
