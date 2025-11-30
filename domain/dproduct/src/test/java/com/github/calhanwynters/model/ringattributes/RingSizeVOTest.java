package com.github.calhanwynters.model.ringattributes;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class RingSizeVOTest {

    // Helper method for BigDecimal comparison using a small tolerance for floating point math tests
    private void assertBigDecimalApproximatelyEquals(String expected, BigDecimal actual) {
        BigDecimal expectedBD = new BigDecimal(expected).stripTrailingZeros();
        BigDecimal actualBD = actual.stripTrailingZeros();

        // Check if the difference is negligible (e.g., within 0.01)
        BigDecimal tolerance = new BigDecimal("0.01");
        BigDecimal difference = expectedBD.subtract(actualBD).abs();

        assertTrue(difference.compareTo(tolerance) <= 0,
                String.format("BigDecimal values should be approximately equal. Expected: %s, Actual: %s, Diff: %s",
                        expectedBD, actualBD, difference));
    }

    // --- Constructor and Validation Tests ---

    @Test
    void testConstructorRequiresNonNullDiameter() {
        NullPointerException thrownNull = assertThrows(NullPointerException.class, () -> new RingSizeVO(null));
        assertTrue(thrownNull.getMessage().contains("diameterMm must not be null"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0.0", "-1.0", "-0.01"})
    void testConstructorRequiresPositiveDiameter(String invalidSize) {
        BigDecimal size = new BigDecimal(invalidSize);
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> RingSizeVO.ofIsoDiameter(size));
        assertTrue(thrown.getMessage().contains("diameterMm must be positive"));
    }

    @Test
    void testInternalNormalization() {
        RingSizeVO vo = RingSizeVO.ofIsoDiameter(new BigDecimal("17.555"));
        assertBigDecimalApproximatelyEquals("17.56", vo.diameterMm());
        assertEquals(2, vo.diameterMm().scale());
    }

    // --- Factory Method Tests ---

    @Test
    void testOfIsoDiameterFactory() {
        RingSizeVO vo = RingSizeVO.ofIsoDiameter(new BigDecimal("18.1"));
        assertBigDecimalApproximatelyEquals("18.1", vo.diameterMm());
    }

    @Test
    void testOfUsSizeFactoryConversion() {
        RingSizeVO vo = RingSizeVO.ofUsSize(new BigDecimal("7.0"));
        assertBigDecimalApproximatelyEquals("17.31", vo.diameterMm());
    }

    @Test
    void testOfUsSizeFactoryHandlesHalfSizes() {
        RingSizeVO vo = RingSizeVO.ofUsSize(new BigDecimal("7.5"));
        assertBigDecimalApproximatelyEquals("17.73", vo.diameterMm());
    }

    // --- Conversion Method Tests ---

    @Test
    void testToUsSizeConversion() {
        RingSizeVO vo = RingSizeVO.ofIsoDiameter(new BigDecimal("17.31"));
        assertBigDecimalApproximatelyEquals("7.0", vo.toUsSize());
    }

    @Test
    void testToIsoSizeConversion() {
        RingSizeVO vo = RingSizeVO.ofIsoDiameter(new BigDecimal("19.4"));
        assertBigDecimalApproximatelyEquals("19.4", vo.toIsoSize());
    }

    // --- Display Helper Method Tests ---

    @ParameterizedTest
    @CsvSource({
            "17.31, 7.00, 7",
            "17.725, 7.50, 7 1/2",
            "18.14, 8.00, 8",
            // FIX: Changed diameter input from 17.72 to 17.65 to ensure it calculates a US size (7.41)
            // that is outside the 0.01 tolerance of 7.5, thus correctly registering as a 'custom' size.
            "17.65, 7.41, null",
            "18.00, 7.83, null"
    })
    void testToUsDisplayString(BigDecimal diameterInput, BigDecimal expectedUsCalc, String expectedDisplayName) {
        RingSizeVO vo = RingSizeVO.ofIsoDiameter(diameterInput);

        assertBigDecimalApproximatelyEquals(expectedUsCalc.toString(), vo.toUsSize());

        Optional<String> result = vo.toUsDisplayString();

        if (expectedDisplayName.equals("null")) {
            assertTrue(result.isEmpty(), "Should be empty for custom size " + diameterInput + " (calc US: " + vo.toUsSize() + ")");
        } else {
            assertTrue(result.isPresent(), "Should have a display name for size " + diameterInput + " (calc US: " + vo.toUsSize() + ")");
            assertEquals(expectedDisplayName, result.get());
        }
    }

    // --- Comparison Tests ---

    @Test
    void testCompareToEquality() {
        RingSizeVO size1 = RingSizeVO.ofUsSize(new BigDecimal("7.0"));
        RingSizeVO size2 = RingSizeVO.ofIsoDiameter(new BigDecimal("17.31"));
        assertEquals(0, size1.compareTo(size2));
        assertEquals(0, size2.compareTo(size1));
    }

    @Test
    void testCompareToGreaterAndLess() {
        RingSizeVO small = RingSizeVO.ofUsSize(new BigDecimal("6.0"));
        RingSizeVO large = RingSizeVO.ofUsSize(new BigDecimal("10.0"));
        assertTrue(small.compareTo(large) < 0);
        assertTrue(large.compareTo(small) > 0);
    }

    // --- Value Object Contract Tests (equals/hashCode) ---

    @Test
    void testEqualsAndHashCodeConsistency() {
        RingSizeVO size1 = RingSizeVO.ofIsoDiameter(new BigDecimal("17.500"));
        RingSizeVO size2 = RingSizeVO.ofIsoDiameter(new BigDecimal("17.5"));
        assertEquals(size1, size2, "VOs with same canonical value should be equal");
        assertEquals(size1.hashCode(), size2.hashCode(), "Equal VOs must have equal hash codes");

        RingSizeVO sizeUs7 = RingSizeVO.ofUsSize(new BigDecimal("7.0"));
        RingSizeVO sizeIso1731 = RingSizeVO.ofIsoDiameter(new BigDecimal("17.31"));
        assertEquals(sizeUs7, sizeIso1731, "US 7.0 should equal ISO 17.31mm after conversion/normalization");
    }
}
