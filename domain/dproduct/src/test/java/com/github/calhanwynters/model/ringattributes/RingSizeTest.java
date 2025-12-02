package com.github.calhanwynters.model.ringattributes;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class RingSizeTest {

    // Removed the unused PRECISION_DELTA field.

    @Test
    public void testGetDisplayString() {
        assertEquals("7", RingSize.NA_SIZE_7.getDisplayString());
        assertEquals("P", RingSize.UK_AUS_SIZE_P.getDisplayString());
        assertEquals("54", RingSize.EUR_SIZE_54.getDisplayString());
    }

    @Test
    public void testGetIsoDiameterMm() {
        // Test a known diameter value with high precision
        assertEquals(new BigDecimal("17.3"), RingSize.NA_SIZE_7.getIsoDiameterMm());
        assertEquals(new BigDecimal("17.19"), RingSize.EUR_SIZE_54.getIsoDiameterMm());
    }

    @Test
    public void testGetRegion() {
        assertEquals("NA", RingSize.NA_SIZE_7.getRegion());
        assertEquals("UK/AUS", RingSize.UK_AUS_SIZE_P.getRegion());
    }

    @Test
    public void testGetCircumferenceMm() {
        // Circumference = Diameter * PI (raw, high-precision value)
        BigDecimal expectedCircumference = new BigDecimal("17.3").multiply(new BigDecimal("3.1415926535897932384626433832795028841971"));

        // Use precision delta for comparison since PI is very long
        BigDecimal actualCircumference = RingSize.NA_SIZE_7.getCircumferenceMm();

        // Assert that the raw calculated values are close enough within our defined precision
        // Compares the absolute difference to a very small threshold
        Assertions.assertTrue(expectedCircumference.subtract(actualCircumference).abs().compareTo(new BigDecimal("0.0000000001")) < 0);
    }

    @Test
    public void testFromDisplayString_Success() {
        Optional<RingSize> foundSizeNA = RingSize.fromDisplayString("NA", "8 1/2");
        assertTrue(foundSizeNA.isPresent());
        assertEquals(RingSize.NA_SIZE_8_5, foundSizeNA.get());

        Optional<RingSize> foundSizeUK = RingSize.fromDisplayString("UK/AUS", "O");
        assertTrue(foundSizeUK.isPresent());
        assertEquals(RingSize.UK_AUS_SIZE_O, foundSizeUK.get());
    }

    @Test
    public void testFromDisplayString_CaseInsensitive() {
        Optional<RingSize> foundSizeLower = RingSize.fromDisplayString("na", "8");
        assertTrue(foundSizeLower.isPresent());
        assertEquals(RingSize.NA_SIZE_8, foundSizeLower.get());

        Optional<RingSize> foundSizeMixed = RingSize.fromDisplayString("EuR", "52");
        assertTrue(foundSizeMixed.isPresent());
        assertEquals(RingSize.EUR_SIZE_52, foundSizeMixed.get());
    }

    @Test
    public void testFromDisplayString_Failure() {
        Optional<RingSize> notFoundRegion = RingSize.fromDisplayString("US", "10");
        assertFalse(notFoundRegion.isPresent());

        Optional<RingSize> notFoundSize = RingSize.fromDisplayString("NA", "Z");
        assertFalse(notFoundSize.isPresent());

        Optional<RingSize> nullInputs = RingSize.fromDisplayString(null, null);
        assertFalse(nullInputs.isPresent());
    }

    @Test
    public void testToRegion_Conversion() {
        // NA Size 7 (17.3mm diameter) should convert closely to the EUR size 54 (17.19mm) or 55 (17.51mm).
        // 54 is closer (diff 0.11) than 55 (diff 0.21).
        Optional<RingSize> convertedToEUR = RingSize.NA_SIZE_7.toRegion("EUR");
        assertTrue(convertedToEUR.isPresent());
        assertEquals(RingSize.EUR_SIZE_54, convertedToEUR.get());

        // NA Size 7.5 (17.7mm diameter) should convert closely to the UK/AUS size O (17.6mm) or O 1/2 (17.8mm)
        // Both have a difference of 0.1mm.
        Optional<RingSize> convertedToUK = RingSize.NA_SIZE_7_5.toRegion("UK/AUS");
        assertTrue(convertedToUK.isPresent());
        // Since both O and O_HALF are equally close, we accept either as correct behavior for the min() comparator
        assertTrue(convertedToUK.get() == RingSize.UK_AUS_SIZE_O || convertedToUK.get() == RingSize.UK_AUS_SIZE_O_HALF);


        // Test case sensitivity for the target region parameter
        Optional<RingSize> convertedToGerman = RingSize.NA_SIZE_9.toRegion("german");
        assertTrue(convertedToGerman.isPresent());
        // NA Size 9 is 19.0mm diameter. German size 19.0 is a direct match.
        assertEquals(RingSize.GERMAN_SIZE_19_0, convertedToGerman.get());
    }

    @Test
    public void testToRegion_InvalidTarget() {
        Optional<RingSize> convertedToInvalid = RingSize.NA_SIZE_7.toRegion("XYZ");
        assertFalse(convertedToInvalid.isPresent());

        Optional<RingSize> convertedToNull = RingSize.NA_SIZE_7.toRegion(null);
        assertFalse(convertedToNull.isPresent());
    }

    @Test
    public void testToString() {
        // This tests the scaled/rounded output for display purposes
        String expectedToStringNA = "7 1/2 (Diameter: 17.70 mm, Region: NA)";
        assertEquals(expectedToStringNA, RingSize.NA_SIZE_7_5.toString());

        String expectedToStringEUR = "52 (Diameter: 16.55 mm, Region: EUR)";
        assertEquals(expectedToStringEUR, RingSize.EUR_SIZE_52.toString());
    }
}
