package com.github.calhanwynters.model.anklet;

import com.github.calhanwynters.model.shared.valueobjects.CareInstructionVO;
import com.github.calhanwynters.model.shared.valueobjects.DescriptionVO;
import com.github.calhanwynters.model.shared.valueobjects.GemstoneVO;
import com.github.calhanwynters.model.shared.valueobjects.MaterialCompositionVO;
import com.github.calhanwynters.model.shared.valueobjects.PriceVO;

import java.util.*;

public record AnkletItem(
        AnkletId id,
        AnkletSizeVO size,
        AnkletStyleVO style,
        PriceVO price,
        Set<MaterialCompositionVO> materials,
        Set<GemstoneVO> gemstones,
        DescriptionVO description,
        CareInstructionVO careInstructions
) {
    public AnkletItem {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(size, "size must not be null");
        Objects.requireNonNull(style, "style must not be null");
        Objects.requireNonNull(price, "price must not be null");
        Objects.requireNonNull(materials, "materials must not be null");
        Objects.requireNonNull(gemstones, "gemstones must not be null");
        Objects.requireNonNull(description, "description must not be null");
        Objects.requireNonNull(careInstructions, "careInstructions must not be null"); // Check the new field

        materials = Set.copyOf(materials);
        gemstones = Set.copyOf(gemstones);
    }

    // Updated Factory method: Now *accepts* the instructions as a parameter
    public static AnkletItem create(
            AnkletSizeVO size,
            AnkletStyleVO style,
            PriceVO price,
            Set<MaterialCompositionVO> materials,
            DescriptionVO description,
            CareInstructionVO careInstructions // <-- Required input
    ) {
        // Pass the provided instructions directly into the constructor
        return new AnkletItem(AnkletId.generate(), size, style, price, materials, Collections.emptySet(), description, careInstructions);
    }

    // --- Behavior Methods: Must update existing ones to include careInstructions field ---

    public AnkletItem changePrice(PriceVO newPrice) {
        return new AnkletItem(this.id, this.size, this.style, newPrice, this.materials, this.gemstones, this.description, this.careInstructions);
    }

    // ... (other behavior methods need updating) ...

    /**
     * Adds a material composition. Instructions are NOT recalculated.
     */
    public AnkletItem addMaterial(MaterialCompositionVO material) {
        Set<MaterialCompositionVO> newMaterials = new HashSet<>(this.materials);
        newMaterials.add(material);

        // Pass existing instructions through
        return new AnkletItem(this.id, this.size, this.style, this.price, newMaterials, this.gemstones, this.description, this.careInstructions);
    }

    /**
     * Updates the care instructions (e.g., after expert consultation).
     * @param newInstructions The updated care instructions.
     * @return A new AnkletItem instance with updated instructions.
     */
    public AnkletItem changeCareInstructions(CareInstructionVO newInstructions) {
        return new AnkletItem(this.id, this.size, this.style, this.price, this.materials, this.gemstones, this.description, newInstructions);
    }

    // ... (any other methods need updating to pass this.careInstructions) ...
}
