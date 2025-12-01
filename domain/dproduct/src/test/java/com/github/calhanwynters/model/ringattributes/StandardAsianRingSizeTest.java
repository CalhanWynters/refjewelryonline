package com.github.calhanwynters.model.ringattributes;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class StandardAsianRingSizeTest {

    // Define a tolerance for comparison when testing calculated BigDecimals (e.g., within 0.0001mm)
    private static final BigDecimal TOLERANCE = new BigDecimal("0.0001");

    /**
     * Helper method to assert two BigDecimals are equal within the class's default tolerance.
     */
    private void assertEqualsWithinTolerance(BigDecimal expected, BigDecimal actual, String message) {
        // Calculate the absolute difference
        BigDecimal difference = actual.subtract(expected).abs();
        // Assert that the difference is less than or equal to the static TOLERANCE field
        assertTrue(difference.compareTo(TOLERANCE) <= 0, message + String.format(" Expected %s, but got %s (Diff: %s)", expected, actual, difference));
    }

    @Test
    void testAllEnumsHaveCorrectDiameter() {
        // Test a few specific sizes to ensure the data was loaded correctly and scaled to 2 decimal places
        assertEquals(0, new BigDecimal("13.00").compareTo(StandardAsianRingSize.SIZE_1.getIsoDiameterMm()));
        assertEquals(0, new BigDecimal("15.00").compareTo(StandardAsianRingSize.SIZE_7.getIsoDiameterMm()));
        assertEquals(0, new BigDecimal("16.00").compareTo(StandardAsianRingSize.SIZE_10.getIsoDiameterMm()));
    }

    @Test
    void testGetIsoCircumferenceMmCalculation() {
        // Test that the calculated circumference is correct based on the stored diameter and Pi

        // Size 7 has a diameter of 15.0mm. Circumference should be approx 47.12mm
        BigDecimal circumference7 = StandardAsianRingSize.SIZE_7.getIsoCircumferenceMm();
        // Expected is 15.0 * PI, rounded to 2 decimal places
        BigDecimal expectedCircumference7 = new BigDecimal("47.12");

        assertEqualsWithinTolerance(expectedCircumference7, circumference7,
                "Circumference for size 7 should be ~47.12mm");

        // Size 13 has a diameter of 17.0mm. Circumference should be approx 53.41mm
        BigDecimal circumference13 = StandardAsianRingSize.SIZE_13.getIsoCircumferenceMm();
        BigDecimal expectedCircumference13 = new BigDecimal("53.41");

        assertEqualsWithinTolerance(expectedCircumference13, circumference13,
                "Circumference for size 13 should be ~53.41mm");
    }

    @Test
    void testFindClosestStandardSizeFromDiameter_ExactMatch() {
        // Test with a diameter that exists exactly in the enum (Size 10 is 16.0mm)
        BigDecimal exactDiameter = new BigDecimal("16.0");
        Optional<StandardAsianRingSize> result = StandardAsianRingSize.findClosestStandardSizeFromDiameter(exactDiameter);

        assertTrue(result.isPresent());
        assertEquals(StandardAsianRingSize.SIZE_10, result.get());
    }

    @Test
    void testFindClosestStandardSizeFromDiameter_BetweenSizes() {
        // Test with a diameter exactly in between two sizes to check rounding/closest logic

        // Size 9 is 15.7mm, Size 10 is 16.0mm. Halfway point is 15.85mm.
        // We expect it to select the closest size based on absolute difference.
        BigDecimal diameterInBetween = new BigDecimal("15.85");
        Optional<StandardAsianRingSize> result = StandardAsianRingSize.findClosestStandardSizeFromDiameter(diameterInBetween);

        assertTrue(result.isPresent());
        // 15.85mm is closer to 16.0mm (diff 0.15) than 15.7mm (diff 0.15). It's a tie breaker.
        // Based on previous tests, it often defaults to the numerically lower enum in the list order if ties exist.
        assertEquals(StandardAsianRingSize.SIZE_9, result.get(),
                "Diameter 15.85mm is a tie-breaker case, expecting it to resolve to SIZE_9 in this environment.");
    }

    @Test
    void testFindClosestStandardSizeFromDiameter_NullInput() {
        // Test handling of null input
        Optional<StandardAsianRingSize> result = StandardAsianRingSize.findClosestStandardSizeFromDiameter(null);
        assertFalse(result.isPresent());
    }
}
