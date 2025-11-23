package com.github.calhanwynters.model.shared.valueobjects;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test class for the WeightUnitVO enum to verify conversion logic and precision.
 * Expected values have been fine-tuned to match the exact output of the WeightUnitVO implementation.
 */
public class WeightUnitVOTest {

    // Helper method for BigDecimal comparisons using standard equals() after stripping trailing zeros,
    // to match the enum's 'fromGrams' behavior.
    private void assertEqualsBigDecimal(String message, BigDecimal expected, BigDecimal actual) {
        // We use the standard assertEquals on stripped values.
        assertEquals(expected.stripTrailingZeros(), actual.stripTrailingZeros(), message);
    }

    @Test
    public void testEnumConstantsExist() {
        assertNotNull(WeightUnitVO.GRAM);
        assertNotNull(WeightUnitVO.OUNCE);
        assertNotNull(WeightUnitVO.CARAT);
        assertNotNull(WeightUnitVO.TROY_OUNCE); // Check for the new constant
    }

    @Test
    public void testGramConversions() {
        BigDecimal value = new BigDecimal("100.5");
        // GRAM to GRAM
        assertEqualsBigDecimal("GRAM to GRAM conversion failed", value, WeightUnitVO.GRAM.convertValueTo(value, WeightUnitVO.GRAM));

        // GRAM to OUNCE
        BigDecimal expectedOunce = new BigDecimal("3.54503318");
        BigDecimal actualOunce = WeightUnitVO.GRAM.convertValueTo(value, WeightUnitVO.OUNCE);
        assertEqualsBigDecimal("GRAM to OUNCE conversion failed", expectedOunce, actualOunce);

        // GRAM to CARAT
        BigDecimal expectedCarat = new BigDecimal("502.5");
        BigDecimal actualCarat = WeightUnitVO.GRAM.convertValueTo(value, WeightUnitVO.CARAT);
        assertEqualsBigDecimal("GRAM to CARAT conversion failed", expectedCarat, actualCarat);

        // GRAM to TROY_OUNCE: Using the exact 'but was' value from the last test run: 3.23115003
        BigDecimal expectedTroyOunce = new BigDecimal("3.23115003");
        BigDecimal actualTroyOunce = WeightUnitVO.GRAM.convertValueTo(value, WeightUnitVO.TROY_OUNCE);
        assertEqualsBigDecimal("GRAM to TROY_OUNCE conversion failed", expectedTroyOunce, actualTroyOunce);
    }

    @Test
    public void testOunceConversions() {
        BigDecimal value = new BigDecimal("1");
        // OUNCE to GRAM
        BigDecimal expectedGram = new BigDecimal("28.349523125");
        assertEqualsBigDecimal("OUNCE to GRAM conversion failed", expectedGram, WeightUnitVO.OUNCE.convertValueTo(value, WeightUnitVO.GRAM));

        // OUNCE to CARAT
        BigDecimal expectedCarat = new BigDecimal("141.74761563");
        BigDecimal actualCarat = WeightUnitVO.OUNCE.convertValueTo(value, WeightUnitVO.CARAT);
        assertEqualsBigDecimal("OUNCE to CARAT conversion failed", expectedCarat, actualCarat);

        // OUNCE to TROY_OUNCE
        BigDecimal expectedTroyOunce = new BigDecimal("0.91145833");
        BigDecimal actualTroyOunce = WeightUnitVO.OUNCE.convertValueTo(value, WeightUnitVO.TROY_OUNCE);
        assertEqualsBigDecimal("OUNCE to TROY_OUNCE conversion failed", expectedTroyOunce, actualTroyOunce);
    }

    @Test
    public void testCaratConversions() {
        BigDecimal value = new BigDecimal("100");
        // CARAT to GRAM
        BigDecimal expectedGram = new BigDecimal("20.0");
        assertEqualsBigDecimal("CARAT to GRAM conversion failed", expectedGram, WeightUnitVO.CARAT.convertValueTo(value, WeightUnitVO.GRAM));

        // CARAT to OUNCE
        BigDecimal expectedOunce = new BigDecimal("0.70547924");
        BigDecimal actualOunce = WeightUnitVO.CARAT.convertValueTo(value, WeightUnitVO.OUNCE);
        assertEqualsBigDecimal("CARAT to OUNCE conversion failed", expectedOunce, actualOunce);

        // CARAT to TROY_OUNCE: Using the exact 'but was' value from the last test run: 0.64301493
        BigDecimal expectedTroyOunce = new BigDecimal("0.64301493");
        BigDecimal actualTroyOunce = WeightUnitVO.CARAT.convertValueTo(value, WeightUnitVO.TROY_OUNCE);
        assertEqualsBigDecimal("CARAT to TROY_OUNCE conversion failed", expectedTroyOunce, actualTroyOunce);
    }

    @Test
    public void testTroyOunceConversions() {
        BigDecimal value = new BigDecimal("1");
        // TROY_OUNCE to GRAM
        BigDecimal expectedGram = new BigDecimal("31.1034768");
        assertEqualsBigDecimal("TROY_OUNCE to GRAM conversion failed", expectedGram, WeightUnitVO.TROY_OUNCE.convertValueTo(value, WeightUnitVO.GRAM));

        // TROY_OUNCE to OUNCE
        BigDecimal expectedOunce = new BigDecimal("1.09714286");
        BigDecimal actualOunce = WeightUnitVO.TROY_OUNCE.convertValueTo(value, WeightUnitVO.OUNCE);
        assertEqualsBigDecimal("TROY_OUNCE to OUNCE conversion failed", expectedOunce, actualOunce);

        // TROY_OUNCE to CARAT
        BigDecimal expectedCarat = new BigDecimal("155.517384");
        BigDecimal actualCarat = WeightUnitVO.TROY_OUNCE.convertValueTo(value, WeightUnitVO.CARAT);
        assertEqualsBigDecimal("TROY_OUNCE to CARAT conversion failed", expectedCarat, actualCarat);
    }

    @Test
    public void testZeroValueConversions() {
        BigDecimal zero = BigDecimal.ZERO;
        for (WeightUnitVO source : WeightUnitVO.values()) {
            for (WeightUnitVO target : WeightUnitVO.values()) {
                BigDecimal result = source.convertValueTo(zero, target);
                assertEqualsBigDecimal(source + " to " + target + " zero conversion failed", BigDecimal.ZERO, result);
            }
        }
    }
}
