package com.github.calhanwynters.model.necklaceattributes;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class NecklaceSizeVOTest {

    // Helper for numerical equality (ignores scale)
    private void assertBigDecimalEquals(String expected, BigDecimal actual) {
        assertEquals(0,
                new BigDecimal(expected).compareTo(actual),
                "Numerical values differ: expected=" + expected + ", actual=" + actual);
    }

    // --- Constructor & Validation ---

    @Test
    void testConstructorRequiresNonNull() {
        NullPointerException ex = assertThrows(NullPointerException.class,
                () -> new NecklaceSizeVO(null));
        assertTrue(ex.getMessage().contains("lengthInches must not be null"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0.0", "-1.0", "-0.01"})
    void testConstructorRequiresPositive(String invalid) {
        BigDecimal bd = new BigDecimal(invalid);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> NecklaceSizeVO.ofInches(bd));
        assertTrue(ex.getMessage().contains("lengthInches must be positive"));
    }

    @Test
    void testNormalizationScale() {
        NecklaceSizeVO vo = NecklaceSizeVO.ofInches(new BigDecimal("18.129"));
        assertBigDecimalEquals("18.13", vo.inInches());
        assertEquals(2, vo.inInches().scale());
    }

    // --- Factory Methods ---

    @Test
    void testOfInches() {
        NecklaceSizeVO vo = NecklaceSizeVO.ofInches(new BigDecimal("20.5"));
        assertBigDecimalEquals("20.50", vo.inInches());
        assertEquals(2, vo.inInches().scale());
    }

    @Test
    void testOfCentimeters() {
        // 45.72 cm ↔ exactly 18.00 in with our constant & rounding
        NecklaceSizeVO vo = NecklaceSizeVO.ofCentimeters(new BigDecimal("45.72"));
        assertBigDecimalEquals("18.00", vo.inInches());
        assertEquals(2, vo.inInches().scale());
    }

    // --- Conversions ---

    @Test
    void testInCentimeters() {
        NecklaceSizeVO vo = NecklaceSizeVO.ofInches(new BigDecimal("18"));
        // 18 in → 45.72 cm (scaled to 2 decimals)
        assertBigDecimalEquals("45.72", vo.inCentimeters());
    }

    // --- Standard Necklace Range Classification ---

    @ParameterizedTest
    @CsvSource({
            // Collar
            "13.5, Collar",
            "14.0, Collar",
            // Choker
            "14.1, Choker",
            "15.9, Choker",
            "16.0, Choker",
            // Princess
            "16.1, Princess",
            "18.0, Princess",
            // Matinee
            "18.1, Matinee",
            "20.0, Matinee",
            // Opera
            "20.1, Opera",
            "34.0, Opera",
            // Rope/Lariat
            "34.1, Rope/Lariat",
            "36.0, Rope/Lariat",
            "40.0, Rope/Lariat"
    })
    void testGetStandardLengthName(BigDecimal inches, String expected) {
        NecklaceSizeVO vo = NecklaceSizeVO.ofInches(inches);
        Optional<String> result = vo.getStandardLengthName();

        assertTrue(result.isPresent(),
                "Expected a standard name for " + inches + " but got empty.");
        assertEquals(expected, result.get());
    }

    // --- Comparison ---

    @Test
    void testCompareToEqual() {
        NecklaceSizeVO a = NecklaceSizeVO.ofInches(new BigDecimal("18.0"));
        NecklaceSizeVO b = NecklaceSizeVO.ofCentimeters(new BigDecimal("45.72"));

        assertEquals(0, a.compareTo(b));
        assertEquals(0, b.compareTo(a));
    }

    @Test
    void testCompareToOrdering() {
        NecklaceSizeVO small = NecklaceSizeVO.ofInches(new BigDecimal("16.0"));
        NecklaceSizeVO large = NecklaceSizeVO.ofInches(new BigDecimal("20.0"));

        assertTrue(small.compareTo(large) < 0);
        assertTrue(large.compareTo(small) > 0);
    }

    // --- Equals / HashCode Contract ---

    @Test
    void testEqualsAndHashCode() {
        NecklaceSizeVO a = NecklaceSizeVO.ofInches(new BigDecimal("18.00"));
        NecklaceSizeVO b = NecklaceSizeVO.ofInches(new BigDecimal("18.0"));

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }
}
