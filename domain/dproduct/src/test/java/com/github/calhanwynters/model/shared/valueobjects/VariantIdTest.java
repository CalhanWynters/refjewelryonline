package com.github.calhanwynters.model.shared.valueobjects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VariantIdTest {

    @Test
    void constructsWithValidValue() {
        VariantId variantId = new VariantId("123e4567-e89b-12d3-a456-426614174000");
        assertEquals("123e4567-e89b-12d3-a456-426614174000", variantId.value());
    }

    @Test
    void nullValueThrowsNpe() {
        NullPointerException ex = assertThrows(NullPointerException.class, () -> new VariantId(null));
        assertTrue(ex.getMessage().contains("VariantId value cannot be null"));
    }

    @Test
    void emptyValueThrowsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new VariantId(""));
        assertTrue(ex.getMessage().contains("VariantId value cannot be empty or blank"));
    }

    @Test
    void blankValueThrowsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new VariantId("   "));
        assertTrue(ex.getMessage().contains("VariantId value cannot be empty or blank"));
    }

    @Test
    void generatesUniqueVariantId() {
        VariantId variantId1 = VariantId.generate();
        VariantId variantId2 = VariantId.generate();

        assertNotNull(variantId1);
        assertNotNull(variantId2);
        assertNotEquals(variantId1.value(), variantId2.value(), "Generated Variant IDs should be unique");
    }

    @Test
    void toStringReturnsCorrectValue() {
        VariantId variantId = new VariantId("123e4567-e89b-12d3-a456-426614174000");
        assertEquals("123e4567-e89b-12d3-a456-426614174000", variantId.toString());
    }
}
