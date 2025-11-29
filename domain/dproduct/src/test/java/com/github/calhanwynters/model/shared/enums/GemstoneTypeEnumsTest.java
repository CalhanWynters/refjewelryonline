package com.github.calhanwynters.model.shared.enums;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GemstoneTypeEnumsTest {

    // Define all expected values in a single static array
    private static final GemstoneTypeEnums[] ALL_EXPECTED_VALUES = {
            GemstoneTypeEnums.DIAMOND,
            GemstoneTypeEnums.SAPPHIRE,
            GemstoneTypeEnums.RUBY,
            GemstoneTypeEnums.EMERALD,
            GemstoneTypeEnums.MOONSTONE,
            GemstoneTypeEnums.OPAL,
            GemstoneTypeEnums.TOPAZ,
            GemstoneTypeEnums.GARNET,
            GemstoneTypeEnums.PERIDOT,
            GemstoneTypeEnums.AQUAMARINE,
            GemstoneTypeEnums.OTHER
    };

    // The count is derived from the array, not hardcoded separately
    private static final int EXPECTED_COUNT = ALL_EXPECTED_VALUES.length;

    @Test
    public void testEnumValuesCount() {
        // Test that the total number of defined enums matches our expectation
        assertEquals(EXPECTED_COUNT, GemstoneTypeEnums.values().length,
                "Enum count does not match the expected number of defined types.");
    }

    @Test
    public void testSpecificEnumPresence() {
        // Use a Set for efficient O(1) lookups to ensure all expected enums are present
        Set<GemstoneTypeEnums> actualValues = Arrays.stream(GemstoneTypeEnums.values())
                .collect(Collectors.toSet());

        for (GemstoneTypeEnums expected : ALL_EXPECTED_VALUES) {
            assertTrue(actualValues.contains(expected),
                    "Expected gemstone type not found: " + expected);
        }
    }

    @ParameterizedTest
    @MethodSource("validGemstoneNames")
    public void testValueOfValidInputs(String gemName) {
        // Verify each valid gem name can be resolved to its corresponding enum
        assertEquals(GemstoneTypeEnums.valueOf(gemName), GemstoneTypeEnums.valueOf(gemName));
    }

    @Test
    public void testValueOfInvalidInputThrowsException() {
        // Test to ensure valueOf throws an exception for invalid inputs (negative test case)
        assertThrows(IllegalArgumentException.class, () ->
                GemstoneTypeEnums.valueOf("INVALID_GEMSTONE"));
    }

    // Providing a data source for parameterized tests
    static List<String> validGemstoneNames() {
        return Arrays.stream(ALL_EXPECTED_VALUES)
                .map(Enum::name)
                .collect(Collectors.toList());
    }
}
