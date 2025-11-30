package com.github.calhanwynters.model.hairaccessoryattributes;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class HairAccessorySizeVOTest {

    // Helper method for BigDecimal comparison in tests
    private void assertBigDecimalEquals(String expected, BigDecimal actual) {
        // Compare with scale stripped for numerical equality assertion
        assertEquals(0, new BigDecimal(expected).compareTo(actual.stripTrailingZeros()),
                "BigDecimal values should be numerically equal");
    }

    // --- Constructor and Validation Tests ---

    @Test
    void testConstructorRequiresNonNullLength() {
        NullPointerException thrownNull = assertThrows(NullPointerException.class, () -> new HairAccessorySizeVO(null, BigDecimal.ONE, "Small"));
        assertTrue(thrownNull.getMessage().contains("lengthMm must not be null"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0.0", "-5.0", "-0.01"})
    void testConstructorRequiresPositiveLength(String invalidSize) {
        BigDecimal size = new BigDecimal(invalidSize);
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> new HairAccessorySizeVO(size, null, null));
        assertTrue(thrown.getMessage().contains("Length must be positive"));
    }

    @Test
    void testInternalNormalizationOfLengthAndWidth() {
        // Test length normalization
        HairAccessorySizeVO vo = new HairAccessorySizeVO(new BigDecimal("10.123"), null, null);
        assertBigDecimalEquals("10.12", vo.lengthMm());

        // Test width normalization
        HairAccessorySizeVO voWithWidth = new HairAccessorySizeVO(BigDecimal.TEN, new BigDecimal("2.5000"), null);
        assertBigDecimalEquals("2.5", voWithWidth.widthMm());
    }

    // --- Factory Method Tests ---

    @Test
    void testOfLengthFactory() {
        HairAccessorySizeVO vo = HairAccessorySizeVO.ofLength(new BigDecimal("50"), "Large Barrette");

        assertBigDecimalEquals("50", vo.lengthMm());
        assertEquals("Large Barrette", vo.descriptionLabel());
        assertTrue(vo.getWidthMm().isEmpty());
        assertTrue(vo.getDescriptionLabel().isPresent());
    }

    // --- Accessor Tests ---

    @Test
    void testOptionalAccessors() {
        HairAccessorySizeVO fullVo = new HairAccessorySizeVO(BigDecimal.TEN, BigDecimal.ONE, "Medium Clip");

        // Check presence for all fields
        assertTrue(fullVo.getWidthMm().isPresent());
        assertTrue(fullVo.getDescriptionLabel().isPresent());

        assertEquals(BigDecimal.ONE.stripTrailingZeros(), fullVo.getWidthMm().get());
        assertEquals("Medium Clip", fullVo.getDescriptionLabel().get());

        HairAccessorySizeVO sparseVo = new HairAccessorySizeVO(BigDecimal.TEN, null, null);

        // Check absence for optional fields
        assertTrue(sparseVo.getWidthMm().isEmpty());
        assertTrue(sparseVo.getDescriptionLabel().isEmpty());
    }

    // --- Value Object Contract Tests (equals/hashCode) ---
    // Assuming you will add equals/hashCode overrides if needed, otherwise record defaults are fine for basic checks.

    @Test
    void testEqualsAndHashCodeConsistency() {
        // Two objects representing the same numerical values and labels should be equal by default record implementation
        HairAccessorySizeVO size1 = new HairAccessorySizeVO(new BigDecimal("10.00"), new BigDecimal("2.0"), "Medium");
        HairAccessorySizeVO size2 = new HairAccessorySizeVO(new BigDecimal("10"), new BigDecimal("2.000"), "Medium");

        assertEquals(size1, size2, "VOs with same values should be equal");
        assertEquals(size1.hashCode(), size2.hashCode(), "Equal VOs must have equal hash codes");

        // Different length
        HairAccessorySizeVO sizeLarge = new HairAccessorySizeVO(new BigDecimal("20.0"), BigDecimal.valueOf(2), "Large");
        assertNotEquals(size1, sizeLarge);

        // Different label (records DO include labels in equality checks by default)
        HairAccessorySizeVO sizeDifferentLabel = new HairAccessorySizeVO(new BigDecimal("10"), new BigDecimal("2"), "Small");
        assertNotEquals(size1, sizeDifferentLabel, "Records treat labels as part of the value contract by default.");

        // Different width (records DO include width in equality checks by default)
        HairAccessorySizeVO sizeDifferentWidth = new HairAccessorySizeVO(new BigDecimal("10"), new BigDecimal("3"), "Medium");
        assertNotEquals(size1, sizeDifferentWidth);
    }
}
