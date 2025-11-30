package com.github.calhanwynters.model.necklaceattributes;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class NecklaceStyleVOTest {

    // --- Constructor and Validation Tests ---

    @Test
    void testConstructorThrowsForNullStylesSet() {
        assertThrows(NullPointerException.class, () -> new NecklaceStyleVO(null));
    }

    @Test
    void testConstructorThrowsForInvalidStyle() {
        Set<String> invalidSet = new HashSet<>();
        invalidSet.add("Chain"); // Valid style, different case
        invalidSet.add("InvalidMaterial"); // Invalid style

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> new NecklaceStyleVO(invalidSet));
        assertTrue(thrown.getMessage().contains("Invalid style encountered: INVALIDMATERIAL"));
    }

    @Test
    void testConstructorNormalizesAndValidates() {
        Set<String> inputStyles = new HashSet<>();
        inputStyles.add("  CHaIN  "); // Test stripping and casing
        inputStyles.add("Beaded");    // Test casing
        inputStyles.add(null);       // Test null filtering

        NecklaceStyleVO vo = new NecklaceStyleVO(inputStyles);
        Set<String> expectedNormalized = Set.of("CHAIN", "BEADED");

        assertEquals(expectedNormalized, vo.styles(), "Styles should be normalized to uppercase and stored as an immutable set.");
    }

    @Test
    void testConstructorCreatesImmutableInternalSet() {
        HashSet<String> mutableInputSet = new HashSet<>();
        mutableInputSet.add("LARIAT");

        NecklaceStyleVO vo = new NecklaceStyleVO(mutableInputSet);

        // Try to modify the set retrieved via the accessor (should fail)
        assertThrows(UnsupportedOperationException.class, () -> vo.styles().add("CHOKER"));
    }

    @Test
    void testConstructorHandlesEmptyStringsInInput() {
        Set<String> inputStyles = Set.of("PENDANT", " ", "");
        NecklaceStyleVO vo = new NecklaceStyleVO(inputStyles);
        Set<String> expected = Set.of("PENDANT");
        assertEquals(expected, vo.styles());
    }

    // --- Factory Method Tests ---

    @Test
    void testOfSingleStyleFactory() {
        NecklaceStyleVO vo = NecklaceStyleVO.of("Pendant");
        assertTrue(vo.hasStyle("PENDANT"));
        assertEquals(1, vo.styles().size());
    }

    @Test
    void testOfSetFactory() {
        Set<String> inputStyles = Set.of("Riviera", "Opera");
        NecklaceStyleVO vo = NecklaceStyleVO.of(inputStyles);
        assertTrue(vo.hasStyle("RIVIERA"));
        assertTrue(vo.hasStyle("OPERA"));
        assertEquals(2, vo.styles().size());
    }

    // --- Behavior Method Tests ---

    @ParameterizedTest
    @CsvSource({
            "PENDANT, true",
            "pendant, true",
            " PENDAnt , true",
            "CHAIN, true",
            "Beaded, true",
            "invalid, false",
            "null, false"
    })
    void testHasStyleMethod(String styleToCheck, boolean expectedResult) {
        // We initialize a VO with multiple styles to test hasStyle robustly
        NecklaceStyleVO vo = new NecklaceStyleVO(Set.of("PENDANT", "CHAIN", "BEADED"));

        // Handle the string "null" case from CsvSource for null input test
        if ("null".equals(styleToCheck)) {
            assertFalse(vo.hasStyle(null), "hasStyle should return false for null input");
        } else {
            assertEquals(expectedResult, vo.hasStyle(styleToCheck));
        }
    }

    @Test
    void testDisplayNameMethodMultipleStyles() {
        Set<String> inputStyles = new HashSet<>();
        inputStyles.add("PENDANT");
        inputStyles.add("RIVIERA");
        inputStyles.add("COLLAR");

        NecklaceStyleVO vo = new NecklaceStyleVO(inputStyles);
        String displayName = vo.displayName();

        // The order might vary in a Set, so check expected components and general format
        assertTrue(displayName.contains("Pendant"));
        assertTrue(displayName.contains("Riviera"));
        assertTrue(displayName.contains("Collar"));
        // Ensure commas separate exactly 3 items
        assertEquals(2, displayName.chars().filter(ch -> ch == ',').count());
    }

    @Test
    void testDisplayNameMethodSingleStyle() {
        NecklaceStyleVO vo = NecklaceStyleVO.of("LARIAT");
        assertEquals("Lariat", vo.displayName());
    }

    @Test
    void testDisplayNameMethodEmptyStyles() {
        NecklaceStyleVO vo = new NecklaceStyleVO(Collections.emptySet());
        assertEquals("", vo.displayName());
    }
}