package com.github.calhanwynters.model.shared.enums;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class VariantStatusEnumsTest {

    // Define all expected values in a single static array as the source of truth
    private static final VariantStatusEnums[] ALL_EXPECTED_VALUES = {
            VariantStatusEnums.ACTIVE,
            VariantStatusEnums.DRAFT,
            VariantStatusEnums.INACTIVE,
            VariantStatusEnums.DISCONTINUED
    };

    // The expected count is derived from the array length
    private static final int EXPECTED_COUNT = ALL_EXPECTED_VALUES.length;

    @Test
    public void testEnumValuesCount() {
        // Test that the total number of defined enums matches our expectation
        assertEquals(EXPECTED_COUNT, VariantStatusEnums.values().length,
                "Enum count does not match the expected number of defined types.");
    }

    @Test
    public void testSpecificEnumPresence() {
        // Use a Set for efficient O(1) lookups to ensure all expected enums are present
        Set<VariantStatusEnums> actualValues = Arrays.stream(VariantStatusEnums.values())
                .collect(Collectors.toSet());

        for (VariantStatusEnums expected : ALL_EXPECTED_VALUES) {
            assertTrue(actualValues.contains(expected),
                    "Expected variant status not found: " + expected);
        }
    }

    @ParameterizedTest
    @MethodSource("validStatusNames")
    public void testValueOfValidInputs(String statusName) {
        // Verify each valid name can be resolved by Enum.valueOf() without throwing an exception
        assertDoesNotThrow(() -> VariantStatusEnums.valueOf(statusName));
        // Check that the resolved value matches the string input
        assertEquals(statusName, VariantStatusEnums.valueOf(statusName).name());
    }

    @Test
    public void testValueOfInvalidInputThrowsException() {
        // Test to ensure valueOf throws an exception for invalid inputs (negative test case)
        assertThrows(IllegalArgumentException.class, () ->
                VariantStatusEnums.valueOf("NON_EXISTENT_STATUS"));
    }

    // Providing a data source for parameterized tests
    static List<String> validStatusNames() {
        return Arrays.stream(ALL_EXPECTED_VALUES)
                .map(Enum::name)
                .collect(Collectors.toList());
    }
}
