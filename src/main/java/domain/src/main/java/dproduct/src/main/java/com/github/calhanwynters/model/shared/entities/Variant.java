package com.github.calhanwynters.model.shared.entities;

import com.github.calhanwynters.model.shared.valueobjects.*;

import java.util.Objects;
import java.util.Set;

public interface Variant {
    VariantId id();
    PriceVO price();
    CareInstructionVO careInstructions();
    Set<MaterialCompositionVO> materials();
    Set<GemstoneVO> gemstones();
}
