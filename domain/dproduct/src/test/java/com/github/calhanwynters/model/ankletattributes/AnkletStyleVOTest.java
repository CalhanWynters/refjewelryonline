package com.github.calhanwynters.model.ankletattributes;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AnkletStyleVOTest {

    // --- Constructor and Validation Tests ---

    @Test
    void testConstructorThrowsForNullStylesSet() {
        assertThrows(NullPointerException.class, () -> new AnkletStyleVO(null));
    }

    @Test
    void testConstructorThrowsForInvalidStyle() {
        Set<String> invalidSet = new HashSet<>();
        invalidSet.add("Beaded"); // Valid style, different case
        invalidSet.add("InvalidMaterial"); // Invalid style

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> new AnkletStyleVO(invalidSet));
        assertTrue(thrown.getMessage().contains("Invalid style encountered: INVALIDMATERIAL"));
    }

    @Test
    void testConstructorNormalizesAndValidates() {
        Set<String> inputStyles = new HashSet<>();
        inputStyles.add("  chaIn  "); // Test stripping and casing
        inputStyles.add("Beaded"); // Test casing
        inputStyles.add(null); // Test null filtering

        AnkletStyleVO vo = new AnkletStyleVO(inputStyles);
        Set<String> expectedNormalized = Set.of("CHAIN", "BEADED");

        assertEquals(expectedNormalized, vo.styles(), "Styles should be normalized to uppercase and stored as an immutable set.");
    }

    @Test
    void testConstructorCreatesImmutableInternalSet() {
        HashSet<String> mutableInputSet = new HashSet<>();
        mutableInputSet.add("ROPE");

        AnkletStyleVO vo = new AnkletStyleVO(mutableInputSet);

        // Try to modify the set retrieved via the accessor (should fail)
        assertThrows(UnsupportedOperationException.class, () -> vo.styles().add("PEARL"));
    }

    @Test
    void testConstructorHandlesEmptyStringsInInput() {
        Set<String> inputStyles = Set.of("CUFF", " ", "");
        AnkletStyleVO vo = new AnkletStyleVO(inputStyles);
        Set<String> expected = Set.of("CUFF");
        assertEquals(expected, vo.styles());
    }

    // --- Factory Method Tests ---

    @Test
    void testOfSingleStyleFactory() {
        AnkletStyleVO vo = AnkletStyleVO.of("Charm");
        assertTrue(vo.hasStyle("CHARM"));
        assertEquals(1, vo.styles().size());
    }

    @Test
    void testOfSetFactory() {
        Set<String> inputStyles = Set.of("Link", "Rope");
        AnkletStyleVO vo = AnkletStyleVO.of(inputStyles);
        assertTrue(vo.hasStyle("LINK"));
        assertTrue(vo.hasStyle("ROPE"));
        assertEquals(2, vo.styles().size());
    }

    // --- Behavior Method Tests ---

    @ParameterizedTest
    @CsvSource({
            "BEADED, true",
            "beaded, true",
            " BEadEd , true",
            "CHAIN, true",
            "Gemstone, true",
            "invalid, false",
            "null, false"
    })
    void testHasStyleMethod(String styleToCheck, boolean expectedResult) {
        // We initialize a VO with multiple styles to test hasStyle robustly
        AnkletStyleVO vo = new AnkletStyleVO(Set.of("BEADED", "CHAIN", "GEMSTONE"));

        // Handle the string "null" case from CsvSource for null input test
        if ("null".equals(styleToCheck)) {
            assertFalse(vo.hasStyle(null), "hasStyle should return false for null input");
        } else {
            assertEquals(expectedResult, vo.hasStyle(styleToCheck));
        }
    }

    @Test
    void testDisplayNameMethod() {
        Set<String> inputStyles = new HashSet<>();
        inputStyles.add("ROPE");
        inputStyles.add("CUFF");
        inputStyles.add("SLIDER");

        AnkletStyleVO vo = new AnkletStyleVO(inputStyles);
        String displayName = vo.displayName();

        // The order might vary in a Set, so check all possibilities for the comma-separated string
        assertTrue(displayName.equals("Rope, Cuff, Slider") ||
                displayName.equals("Rope, Slider, Cuff") ||
                displayName.equals("Cuff, Rope, Slider") ||
                displayName.equals("Cuff, Slider, Rope") ||
                displayName.equals("Slider, Rope, Cuff") ||
                displayName.equals("Slider, Cuff, Rope"));
    }

    @Test
    void testDisplayNameMethodSingleStyle() {
        AnkletStyleVO vo = AnkletStyleVO.of("PEARL");
        assertEquals("Pearl", vo.displayName());
    }

    @Test
    void testDisplayNameMethodEmptyStyles() {
        // It's not clear if an empty set is valid by design (constructor technically allows it if passed)
        // Assuming it can happen for some edge case, though normal usage prevents it if passed a non-empty set.
        AnkletStyleVO vo = new AnkletStyleVO(Collections.emptySet());
        assertEquals("", vo.displayName());
    }
}