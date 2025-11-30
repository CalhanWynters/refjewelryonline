package com.github.calhanwynters.model.shared.valueobjects;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for WeightVO to validate its functionality.
 */
public class WeightVOTest {

    // Helper method for BigDecimal comparisons using the VO's defined scale and rounding mode
    private void assertEqualsVOValue(String message, String expectedValueString, BigDecimal actualValue) {
        BigDecimal expected = new BigDecimal(expectedValueString)
                .setScale(WeightVO.WeightUnit.SCALE, WeightVO.WeightUnit.ROUNDING_MODE)
                .stripTrailingZeros();

        // The actual value returned should already be scaled/stripped by the VO constructor
        assertEquals(expected, actualValue.stripTrailingZeros(), message);
    }

    @Test
    public void testCreationWithValidValues() {
        WeightVO weight = WeightVO.ofGrams(new BigDecimal("500.0"));
        assertNotNull(weight);
        // The VO should store the normalized amount
        assertEqualsVOValue("Amount not normalized correctly", "500.0", weight.amount());
        assertEquals(WeightVO.WeightUnit.GRAM, weight.unit());
    }

    @Test
    public void testCreationWithInvalidAmount() {
        assertThrows(IllegalArgumentException.class, () ->
                        new WeightVO(new BigDecimal("-1"), WeightVO.WeightUnit.GRAM),
                "Amount must not be negative");
    }

    @Test
    public void testCreationWithNullAmount() {
        assertThrows(NullPointerException.class, () ->
                        new WeightVO(null, WeightVO.WeightUnit.GRAM),
                "Amount must not be null");
    }

    @Test
    public void testCreationWithNullUnit() {
        assertThrows(NullPointerException.class, () ->
                        new WeightVO(new BigDecimal("10"), null),
                "Unit must not be null");
    }

    @Test
    public void testMaxWeightValidation() {
        assertThrows(IllegalArgumentException.class, () ->
                        WeightVO.ofGrams(new BigDecimal("100000.1")),
                "Amount exceeds maximum allowed weight");
    }

    @Test
    public void testConversions() {
        WeightVO weightInOunces = WeightVO.ofOunces(new BigDecimal("1")); // 1 Ounce Avoirdupois

        // Check inGrams() output (rounded to SCALE 4)
        assertEqualsVOValue("inGrams() conversion failed", "28.3495", weightInOunces.inGrams());

        // Convert to Gram unit and check the normalized amount
        WeightVO weightInGrams = weightInOunces.toUnit(WeightVO.WeightUnit.GRAM);
        assertEqualsVOValue("toUnit(GRAM) amount failed", "28.3495", weightInGrams.amount());

        // Convert to Carat unit and check the normalized amount
        WeightVO weightInCarats = weightInGrams.toUnit(WeightVO.WeightUnit.CARAT);
        assertEqualsVOValue("toUnit(CARAT) amount failed", "141.7475", weightInCarats.amount());

        // Convert to Troy Ounce unit and check the normalized amount
        WeightVO weightInTroyOunces = weightInGrams.toUnit(WeightVO.WeightUnit.TROY_OUNCE);
        assertEqualsVOValue("toUnit(TROY_OUNCE) amount failed", "0.9115", weightInTroyOunces.amount());
    }

    @Test
    public void testAddWeights() {
        WeightVO weightA = WeightVO.ofGrams(new BigDecimal("500.0"));
        WeightVO weightB = WeightVO.ofOunces(new BigDecimal("1"));

        WeightVO totalWeight = weightA.add(weightB);
        // Total grams: 500.0 + 28.3495... = 528.3495
        assertEqualsVOValue("Addition failed", "528.3495", totalWeight.inGrams());
    }

    @Test
    public void testSubtractWeights() {
        WeightVO weightA = WeightVO.ofGrams(new BigDecimal("500.0"));
        WeightVO weightB = WeightVO.ofOunces(new BigDecimal("1"));

        WeightVO remainingWeight = weightA.subtract(weightB);
        // Remaining grams: 500.0 - 28.3495... = 471.6505
        assertEqualsVOValue("Subtraction failed", "471.6505", remainingWeight.inGrams());

        // Test subtraction resulting in negative (should throw exception)
        WeightVO smallWeight = WeightVO.ofGrams(new BigDecimal("10"));
        assertThrows(IllegalArgumentException.class, () ->
                        smallWeight.subtract(weightB),
                "Resulting weight must not be negative");
    }

    @Test
    public void testComparison() {
        WeightVO weightA = WeightVO.ofGrams(new BigDecimal("500.0"));
        WeightVO weightB = WeightVO.ofOunces(new BigDecimal("1")); // ~28.35g

        assertTrue(weightA.compareTo(weightB) > 0, "Weight A should be greater than Weight B");
        assertTrue(weightB.compareTo(weightA) < 0, "Weight B should be less than Weight A");
        assertEquals(0, weightA.compareTo(WeightVO.ofGrams(new BigDecimal("500.0"))),
                "Equal weights should return 0");
    }
}


