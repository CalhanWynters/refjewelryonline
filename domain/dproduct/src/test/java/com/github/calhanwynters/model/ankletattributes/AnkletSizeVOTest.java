package com.github.calhanwynters.model.ankletattributes;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AnkletSizeVOTest {

    // Helper method for BigDecimal comparison in tests (compareTo returns 0 for numerical equality)
    private void assertBigDecimalEquals(String expected, BigDecimal actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(actual.stripTrailingZeros()),
                "BigDecimal values should be numerically equal");
    }

    // --- Constructor and Validation Tests ---

    @Test
    void testConstructorRequiresPositiveLength() {
        // Test with zero or negative values, expecting an exception
        IllegalArgumentException thrownZero = assertThrows(IllegalArgumentException.class, () -> AnkletSizeVO.ofInches(BigDecimal.ZERO));
        assertTrue(thrownZero.getMessage().contains("must be positive"));

        IllegalArgumentException thrownNegative = assertThrows(IllegalArgumentException.class, () -> AnkletSizeVO.ofInches(new BigDecimal("-5.0")));
        assertTrue(thrownNegative.getMessage().contains("must be positive"));
    }

    @Test
    void testConstructorRequiresNonNull() {
        // Test with a null input, expecting a NullPointerException
        NullPointerException thrownNull = assertThrows(NullPointerException.class, () -> new AnkletSizeVO(null));
        assertTrue(thrownNull.getMessage().contains("lengthInches must not be null"));
    }

    @Test
    void testInternalNormalization() {
        // Ensure internal storage is scaled and stripped correctly
        AnkletSizeVO vo = AnkletSizeVO.ofInches(new BigDecimal("9.000"));
        assertBigDecimalEquals("9", vo.inInches());
    }

    // --- Factory Method Tests ---

    @Test
    void testOfCentimetersConversion() {
        // 25.4 cm should be approximately 10 inches
        AnkletSizeVO vo = AnkletSizeVO.ofCentimeters(new BigDecimal("25.4"));
        // The implementation scales to 2 decimal places, so we check that precision
        assertBigDecimalEquals("10.00", vo.inInches());
    }

    @Test
    void testOfInchesFactory() {
        AnkletSizeVO vo = AnkletSizeVO.ofInches(new BigDecimal("8.5"));
        assertBigDecimalEquals("8.5", vo.inInches());
    }

    // --- Conversion Method Tests ---

    @Test
    void testInCentimetersConversion() {
        // 9 inches converted to CM
        AnkletSizeVO vo = AnkletSizeVO.ofInches(new BigDecimal("9"));
        // 9 / 0.393701 approx 22.86 cm (scaled to 2 decimal places)
        assertBigDecimalEquals("22.86", vo.inCentimeters());
    }

    // --- Business Logic (Standard Name) Tests ---

    // Use parameterized tests for standard ranges
    @ParameterizedTest
    @CsvSource({
            "8.5, Petite",
            "9.0, Petite", // Boundary check
            "9.1, Standard",
            "10.0, Standard", // Boundary check
            "10.5, Large",
            "11.0, Large", // Boundary check
            "11.1, Extra Large",
            "15.0, Extra Large"
    })
    void testGetStandardLengthName(BigDecimal inches, String expectedName) {
        AnkletSizeVO vo = AnkletSizeVO.ofInches(inches);
        Optional<String> result = vo.getStandardLengthName();
        assertTrue(result.isPresent(), "Should have a standard name for value: " + inches);
        assertEquals(expectedName, result.get());
    }

    @Test
    void testGetStandardLengthNameForCustomSize() {
        // A size far outside standard ranges
        AnkletSizeVO vo = AnkletSizeVO.ofInches(new BigDecimal("3.0"));
        // Note: Current logic returns Optional.of("Petite") for everything <= 9.0
        // The original code does not seem to cover 'Optional.empty()' case for truly custom sizes based on the logic provided.
        // Assuming the logic in the original code is correct:
        assertEquals("Petite", vo.getStandardLengthName().orElse("Custom"));
    }

    // --- Comparison Tests ---

    @Test
    void testCompareToEquality() {
        AnkletSizeVO size1 = AnkletSizeVO.ofInches(new BigDecimal("10.0"));
        AnkletSizeVO size2 = AnkletSizeVO.ofCentimeters(new BigDecimal("25.4")); // approx 10 inches

        // compareTo should return 0 if numerically equal, regardless of scale/source
        assertEquals(0, size1.compareTo(size2));
        assertEquals(0, size2.compareTo(size1));
    }

    @Test
    void testCompareToGreaterAndLess() {
        AnkletSizeVO small = AnkletSizeVO.ofInches(new BigDecimal("9.0"));
        AnkletSizeVO large = AnkletSizeVO.ofInches(new BigDecimal("11.0"));

        // Small compared to large should be < 0 (negative)
        assertTrue(small.compareTo(large) < 0);

        // Large compared to small should be > 0 (positive)
        assertTrue(large.compareTo(small) > 0);
    }
}
