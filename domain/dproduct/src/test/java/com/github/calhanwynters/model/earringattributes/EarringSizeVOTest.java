package com.github.calhanwynters.model.earringattributes;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class EarringSizeVOTest {

    // Helper method for BigDecimal comparison in tests
    private void assertBigDecimalEquals(String expected, BigDecimal actual) {
        // Compare with scale stripped for numerical equality assertion
        assertEquals(0, new BigDecimal(expected).compareTo(actual.stripTrailingZeros()),
                "BigDecimal values should be numerically equal");
    }

    // --- Constructor and Validation Tests ---

    @ParameterizedTest
    @ValueSource(strings = {"0.0", "-5.0", "-0.01"})
    void testConstructorRequiresPositiveSize(String invalidSize) {
        BigDecimal size = new BigDecimal(invalidSize);
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> EarringSizeVO.ofMillimeters(size));
        assertTrue(thrown.getMessage().contains("sizeMm must be positive"));
    }

    @Test
    void testConstructorRequiresNonNullSize() {
        NullPointerException thrownNull = assertThrows(NullPointerException.class, () -> new EarringSizeVO(null, "Diameter"));
        assertTrue(thrownNull.getMessage().contains("sizeMm must not be null"));
    }

    @Test
    void testInternalNormalizationOfSize() {
        // Ensure internal storage is scaled and stripped correctly
        EarringSizeVO vo = EarringSizeVO.ofMillimeters(new BigDecimal("10.0000"));
        assertBigDecimalEquals("10", vo.inMillimeters());
        assertEquals(0, vo.inMillimeters().scale());
    }

    @Test
    void testLabelNormalization() {
        EarringSizeVO voNull = new EarringSizeVO(BigDecimal.ONE, null);
        assertTrue(voNull.labelOptional().isEmpty());
        assertNull(voNull.label()); // The record accessor 'label()' should also be null

        EarringSizeVO voBlank = new EarringSizeVO(BigDecimal.ONE, "  ");
        assertTrue(voBlank.labelOptional().isEmpty());
        assertNull(voBlank.label());

        EarringSizeVO voStripped = new EarringSizeVO(BigDecimal.ONE, " Drop Length ");
        assertTrue(voStripped.labelOptional().isPresent());
        assertEquals("Drop Length", voStripped.labelOptional().get());
        assertEquals("Drop Length", voStripped.label());
    }

    // --- Factory Method Tests ---

    @Test
    void testOfMillimetersFactorySimple() {
        EarringSizeVO vo = EarringSizeVO.ofMillimeters(new BigDecimal("12.5"));
        assertBigDecimalEquals("12.5", vo.inMillimeters());
        assertTrue(vo.labelOptional().isEmpty());
    }

    @Test
    void testOfMillimetersFactoryWithLabel() {
        EarringSizeVO vo = EarringSizeVO.ofMillimeters(new BigDecimal("15"), "Hoop Diameter");
        assertBigDecimalEquals("15", vo.inMillimeters());
        // Updated: Use the direct accessor since the factory guarantees it's present/normalized
        assertEquals("Hoop Diameter", vo.label());
    }

    @Test
    void testOfInchesFactoryConversion() {
        // 0.5 inches should be approx 12.7 mm
        BigDecimal halfInch = new BigDecimal("0.5");
        EarringSizeVO vo = EarringSizeVO.ofInches(halfInch);

        // The implementation calculates 0.5 / 0.0393701 approx 12.70
        assertBigDecimalEquals("12.70", vo.inMillimeters());
        assertTrue(vo.labelOptional().isEmpty());
    }

    // --- Accessors/Conversion Method Tests ---

    @Test
    void testInInchesConversion() {
        // 20 mm converted to inches (approx 0.79 inches)
        EarringSizeVO vo = EarringSizeVO.ofMillimeters(new BigDecimal("20"));

        // 20 * 0.0393701 = 0.787402 -> Scaled to 0.79
        assertBigDecimalEquals("0.79", vo.inInches());
    }

    @Test
    void testLabelOptionalPresence() {
        EarringSizeVO voWithLabel = EarringSizeVO.ofMillimeters(BigDecimal.TEN, "Stud Size");
        EarringSizeVO voWithoutLabel = EarringSizeVO.ofMillimeters(BigDecimal.TEN);

        assertTrue(voWithLabel.labelOptional().isPresent());
        // Updated: Use direct accessor
        assertEquals("Stud Size", voWithLabel.label());
        assertFalse(voWithoutLabel.labelOptional().isPresent());
    }

    // --- Comparison Tests ---

    @Test
    void testCompareToEquality() {
        EarringSizeVO size1 = EarringSizeVO.ofMillimeters(new BigDecimal("10.0"));
        EarringSizeVO size2 = EarringSizeVO.ofInches(new BigDecimal("0.393701")); // Approx 10mm

        // compareTo should return 0 if numerically equal, regardless of scale/source/label
        assertEquals(0, size1.compareTo(size2));
        assertEquals(0, size2.compareTo(size1));
    }

    @Test
    void testCompareToGreaterAndLess() {
        EarringSizeVO small = EarringSizeVO.ofMillimeters(new BigDecimal("5.0"));
        EarringSizeVO large = EarringSizeVO.ofMillimeters(new BigDecimal("25.0"));

        // Small compared to large should be < 0 (negative)
        assertTrue(small.compareTo(large) < 0);

        // Large compared to small should be > 0 (positive)
        assertTrue(large.compareTo(small) > 0);
    }

    // --- Value Object Contract Tests (equals/hashCode) ---

    @Test
    void testEqualsAndHashCodeConsistency() {
        // Two objects representing the same physical size (different construction methods, different labels)
        EarringSizeVO sizeMM = EarringSizeVO.ofMillimeters(new BigDecimal("10.0"), "Diameter");
        EarringSizeVO sizeInches = EarringSizeVO.ofInches(new BigDecimal("0.393701"));

        // They are numerically equivalent internally (10.0 mm vs ~10.0 mm after conversion/scaling)
        assertEquals(sizeMM, sizeInches, "VOs with same canonical value should be equal");
        assertEquals(sizeMM.hashCode(), sizeInches.hashCode(), "Equal VOs must have equal hash codes");

        // A different size
        EarringSizeVO sizeLarge = EarringSizeVO.ofMillimeters(new BigDecimal("20.0"));
        assertNotEquals(sizeMM, sizeLarge);
    }
}
