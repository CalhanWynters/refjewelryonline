package com.github.calhanwynters.model.shared.entities;

import com.github.calhanwynters.model.shared.enums.VariantStatusVO;
import com.github.calhanwynters.model.shared.valueobjects.*;
import java.util.Set;

public interface Variant {
    VariantId id();
    String sku(); // <-- New SKU accessor
    PriceVO basePrice();
    PriceVO currentPrice();
    CareInstructionVO careInstructions();
    Set<MaterialCompositionVO> materials();
    Set<GemstoneVO> gemstones();
    VariantStatusVO status();

    /**
     * Checks if this variant has the same physical attributes as another variant,
     * ignoring identity (ID, SKU) and volatile properties (price, status).
     * This is useful for ensuring uniqueness within a Product aggregate.
     */
    boolean hasSameAttributes(Variant other);
}
