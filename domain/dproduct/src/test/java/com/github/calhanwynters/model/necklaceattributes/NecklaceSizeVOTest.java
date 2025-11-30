package com.github.calhanwynters.model.necklaceattributes;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class NecklaceSizeVOTest {

    // Helper method for BigDecimal comparison in tests
    private void assertBigDecimalEquals(String expected, BigDecimal actual) {
        // We strip trailing zeros before comparison to handle normalization differences (e.g., 10.00 vs 10)
        assertEquals(0, new BigDecimal(expected).compareTo(actual.stripTrailingZeros()),
                "BigDecimal values should be numerically equal");
    }

    // --- Constructor and Validation Tests ---

    @Test
    void testConstructorRequiresNonNull() {
        NullPointerException thrownNull = assertThrows(NullPointerException.class, () -> new NecklaceSizeVO(null));
        assertTrue(thrownNull.getMessage().contains("lengthInches must not be null"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0.0", "-1.0", "-0.01"})
    void testConstructorRequiresPositiveLength(String invalidSize) {
        BigDecimal size = new BigDecimal(invalidSize);
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> NecklaceSizeVO.ofInches(size));
        assertTrue(thrown.getMessage().contains("lengthInches must be positive"));
    }

    @Test
    void testInternalNormalization() {
        // Ensure internal storage is scaled to 2 decimal places exactly as defined in the VO logic
        NecklaceSizeVO vo = NecklaceSizeVO.ofInches(new BigDecimal("18.129"));
        assertBigDecimalEquals("18.13", vo.inInches());
        assertEquals(2, vo.inInches().scale());
    }

    // --- Factory Method Tests ---

    @Test
    void testOfInchesFactory() {
        NecklaceSizeVO vo = NecklaceSizeVO.ofInches(new BigDecimal("20.5"));
        assertBigDecimalEquals("20.5", vo.inInches());
    }

    @Test
    void testOfCentimetersFactoryConversion() {
        // 45.72 cm should be approximately 18 inches
        BigDecimal cmInput = new BigDecimal("45.72");
        NecklaceSizeVO vo = NecklaceSizeVO.ofCentimeters(cmInput);

        // FIX: The VO factory now scales immediately to 2 decimal places.
        // 45.72 cm converts to exactly 18.00 inches in the new logic.
        assertBigDecimalEquals("18.00", vo.inInches());
    }

    // --- Conversion Method Tests ---

    @Test
    void testInCentimetersConversion() {
        // 18 inches converted to CM (approx 45.72 cm)
        NecklaceSizeVO vo = NecklaceSizeVO.ofInches(new BigDecimal("18"));

        // 18 / 0.393701 approx 45.72
        assertBigDecimalEquals("45.72", vo.inCentimeters());
    }

    // --- Business Logic (Standard Name) Tests ---

    // Use parameterized tests for standard ranges, checking the rounding behavior
    @ParameterizedTest
    @CsvSource({
            "13.5, Collar",
            "14.0, Collar",
            "14.1, Choker",
            "15.9, Choker",
            "16.0, Choker",
            "16.1, Princess",
            "18.0, Princess",
            "18.1, Matinee", // FIX: Boundary check updated to match revised VO logic (exclusive lower bound)
            "20.0, Matinee",
            "20.1, Opera",   // FIX: Boundary check updated
            "34.0, Opera",
            "34.1, Rope/Lariat", // FIX: Boundary check updated
            "36.0, Rope/Lariat",
            "40.0, Rope/Lariat"
    })
    void testGetStandardLengthName(BigDecimal inches, String expectedName) {
        NecklaceSizeVO vo = NecklaceSizeVO.ofInches(inches);
        Optional<String> result = vo.getStandardLengthName();
        assertTrue(result.isPresent(), "Should have a standard name for value: " + inches + " Actual lengthInches: " + vo.inInches());
        assertEquals(expectedName, result.get());
    }

    // --- Comparison Tests ---

    @Test
    void testCompareToEquality() {
        NecklaceSizeVO size1 = NecklaceSizeVO.ofInches(new BigDecimal("18.0"));
        // 45.72 cm is now exactly 18.00 inches in the revised factory logic
        NecklaceSizeVO size2 = NecklaceSizeVO.ofCentimeters(new BigDecimal("45.72"));

        // They should be exactly equal now due to consistent normalization in factories
        assertEquals(0, size1.compareTo(size2));
        assertEquals(0, size2.compareTo(size1));
    }

    @Test
    void testCompareToGreaterAndLess() {
        NecklaceSizeVO small = NecklaceSizeVO.ofInches(new BigDecimal("16.0"));
        NecklaceSizeVO large = NecklaceSizeVO.ofInches(new BigDecimal("20.0"));

        assertTrue(small.compareTo(large) < 0);
        assertTrue(large.compareTo(small) > 0);
    }

    // --- Value Object Contract Tests (equals/hashCode) ---

    @Test
    void testEqualsAndHashCodeConsistency() {
        NecklaceSizeVO size1 = NecklaceSizeVO.ofInches(new BigDecimal("18.00"));
        NecklaceSizeVO size2 = NecklaceSizeVO.ofInches(new BigDecimal("18.0"));

        assertEquals(size1, size2, "VOs with same canonical value should be equal");
        assertEquals(size1.hashCode(), size2.hashCode(), "Equal VOs must have equal hash codes");
    }
}
