package com.github.calhanwynters.model.shared.valueobjects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MaterialVOTest {

    @Test
    void constructsWithValidMaterialAndLabel() {
        MaterialVO material = new MaterialVO(MaterialVO.MaterialName.GOLD, "Wedding Band");
        assertEquals(MaterialVO.MaterialName.GOLD, material.material());
        assertEquals("Wedding Band", material.labelOptional().orElse(null));
    }

    @Test
    void constructsWithValidMaterialAndNullLabel() {
        MaterialVO material = new MaterialVO(MaterialVO.MaterialName.SILVER, null);
        assertEquals(MaterialVO.MaterialName.SILVER, material.material());
        assertFalse(material.labelOptional().isPresent(), "Label should be optional and not present");
    }

    @Test
    void validMaterialWithoutLabel() {
        MaterialVO material = MaterialVO.of(MaterialVO.MaterialName.COPPER);
        assertEquals(MaterialVO.MaterialName.COPPER, material.material());
        assertFalse(material.labelOptional().isPresent());
    }

    @Test
    void nullMaterialThrowsNpe() {
        NullPointerException ex = assertThrows(NullPointerException.class, () -> new MaterialVO(null, "Ring"));
        assertTrue(ex.getMessage().contains("material must not be null"));
    }

    @Test
    void otherMaterialRequiresLabel() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new MaterialVO(MaterialVO.MaterialName.OTHER, ""));
        assertTrue(ex.getMessage().contains("label is required when material is OTHER"));
    }

    @Test
    void labelNormalization() {
        MaterialVO material = new MaterialVO(MaterialVO.MaterialName.OTHER, "   Custom Label   ");
        assertEquals("Custom Label", material.labelOptional().orElse(null)); // Should be trimmed
    }

    @Test
    void emptyLabelBecomesNull() {
        MaterialVO material = new MaterialVO(MaterialVO.MaterialName.GOLD, "   ");
        assertFalse(material.labelOptional().isPresent(), "Empty label should normalize to null");
    }

    @Test
    void isPreciousReturnsCorrectValue() {
        assertTrue(new MaterialVO(MaterialVO.MaterialName.GOLD, "Ring").isPrecious());
        assertFalse(new MaterialVO(MaterialVO.MaterialName.BRONZE, "Bracelet").isPrecious());
    }

    @Test
    void displayNameReturnsCorrectLabelOrCanonicalName() {
        MaterialVO materialWithLabel = new MaterialVO(MaterialVO.MaterialName.GOLD, "Wedding Band");
        assertEquals("Wedding Band", materialWithLabel.displayName());

        MaterialVO materialWithoutLabel = new MaterialVO(MaterialVO.MaterialName.SILVER, null);
        assertEquals("Silver", materialWithoutLabel.displayName());
    }

    @Test
    void canonicalNameReturnsCorrectString() {
        assertEquals("Gold", new MaterialVO(MaterialVO.MaterialName.GOLD, null).canonicalName());
        assertEquals("Other", new MaterialVO(MaterialVO.MaterialName.OTHER, "Custom Label").canonicalName());
    }

    @Test
    void withLabelReturnsNewInstanceWithLabel() {
        MaterialVO material = new MaterialVO(MaterialVO.MaterialName.COPPER, null);
        MaterialVO updatedMaterial = material.withLabel("New Label");
        assertEquals("New Label", updatedMaterial.labelOptional().orElse(null));
        assertNull(material.labelOptional().orElse(null), "Original should remain unchanged");
    }

    @Test
    void withoutLabelReturnsNewInstanceWithoutLabel() {
        MaterialVO material = new MaterialVO(MaterialVO.MaterialName.GOLD, "Ring");
        MaterialVO updatedMaterial = material.withoutLabel();
        assertFalse(updatedMaterial.labelOptional().isPresent(), "New instance should have no label");
        assertEquals("Ring", material.labelOptional().orElse(null), "Original should remain unchanged");
    }
}
