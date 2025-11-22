package com.github.calhanwynters.model.shared.valueobjects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MaterialCompositionVOTest {

    @Test
    void constructsWithValidMaterialAndRole() {
        MaterialVO material = new MaterialVO(MaterialVO.MaterialName.GOLD, "Ring");
        String role = "band";
        MaterialCompositionVO composition = new MaterialCompositionVO(material, role);

        assertEquals(material, composition.material());
        assertEquals(role, composition.role());
    }

    @Test
    void nullMaterialThrowsNpe() {
        NullPointerException ex = assertThrows(NullPointerException.class, () -> new MaterialCompositionVO(null, "band"));
        assertTrue(ex.getMessage().contains("material must not be null"));
    }

    @Test
    void nullRoleThrowsNpe() {
        MaterialVO material = new MaterialVO(MaterialVO.MaterialName.SILVER, "Bracelet");
        NullPointerException ex = assertThrows(NullPointerException.class, () -> new MaterialCompositionVO(material, null));
        assertTrue(ex.getMessage().contains("role must not be null"));
    }

    @Test
    void emptyRoleThrowsIllegalArgumentException() {
        MaterialVO material = new MaterialVO(MaterialVO.MaterialName.PLATINUM, "Necklace");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new MaterialCompositionVO(material, ""));
        assertTrue(ex.getMessage().contains("role cannot be empty or blank"));
    }

    @Test
    void blankRoleThrowsIllegalArgumentException() {
        MaterialVO material = new MaterialVO(MaterialVO.MaterialName.OTHER, "Earring");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new MaterialCompositionVO(material, "   "));
        assertTrue(ex.getMessage().contains("role cannot be empty or blank"));
    }
}
