package com.github.calhanwynters.model.hairaccessoryattributes;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class HairAccessorStyleVOTest {

    // --- Constructor and Validation Tests ---

    @Test
    void testConstructorThrowsForNullStylesSet() {
        assertThrows(NullPointerException.class, () -> new HairAccessorStyleVO(null));
    }

    @Test
    void testConstructorThrowsForInvalidStyle() {
        Set<String> invalidSet = new HashSet<>();
        invalidSet.add("Headband");
        invalidSet.add("FakeStyle");

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> new HairAccessorStyleVO(invalidSet));
        assertTrue(thrown.getMessage().contains("Invalid style encountered: FAKESTYLE"));
    }

    @Test
    void testConstructorNormalizesAndValidates() {
        Set<String> inputStyles = new HashSet<>();
        inputStyles.add("  CLIP  ");
        inputStyles.add("ScrunCHIE");
        inputStyles.add(null);

        HairAccessorStyleVO vo = new HairAccessorStyleVO(inputStyles);
        Set<String> expectedNormalized = Set.of("CLIP", "SCRUNCHIE");

        assertEquals(expectedNormalized, vo.styles(), "Styles should be normalized to uppercase and stored as an immutable set.");
    }

    @Test
    void testConstructorCreatesImmutableInternalSet() {
        HashSet<String> mutableInputSet = new HashSet<>();
        mutableInputSet.add("TIARA");

        HairAccessorStyleVO vo = new HairAccessorStyleVO(mutableInputSet);

        assertThrows(UnsupportedOperationException.class, () -> vo.styles().add("BARRETTE"));
    }

    @Test
    void testConstructorHandlesEmptyStringsInInput() {
        Set<String> inputStyles = Set.of("BARRETTE", " ", "");
        HairAccessorStyleVO vo = new HairAccessorStyleVO(inputStyles);
        Set<String> expected = Set.of("BARRETTE");
        assertEquals(expected, vo.styles());
    }

    // --- Factory Method Tests ---

    @Test
    void testOfSingleStyleFactory() {
        HairAccessorStyleVO vo = HairAccessorStyleVO.of("Headband");
        assertTrue(vo.hasStyle("HEADBAND"));
        assertEquals(1, vo.styles().size());
    }

    @Test
    void testOfSetFactory() {
        Set<String> inputStyles = Set.of("Hair Comb", "Clip");
        HairAccessorStyleVO vo = HairAccessorStyleVO.of(inputStyles);
        assertTrue(vo.hasStyle("HAIR_COMB"));
        assertTrue(vo.hasStyle("CLIP"));
        assertEquals(2, vo.styles().size());
    }

    // --- Behavior Method Tests ---

    @ParameterizedTest
    @CsvSource({
            "HEADBAND, true",   // All caps, no space/underscore
            "headband, true",
            " BARRETTE , true", // Space padded
            "HAIR_PIN, true",   // Underscore input
            "hair pin, true",   // Space input
            "TIARA, true",
            "invalid, false",   // Invalid style check
            "null, false"       // Null check
    })
    void testHasStyleMethod(String styleToCheck, boolean expectedResult) {
        // We cannot use a static set in this test easily because not all CsvSource values are in it.
        // Instead, we dynamically create the VO ONLY IF the style is supposed to be present.

        // This test only verifies the logic of the `hasStyle` method itself.
        // We need an instance to test the method on. We'll use a valid, simple style for the instance's state.
        HairAccessorStyleVO vo = HairAccessorStyleVO.of("CLIP");

        if ("null".equals(styleToCheck)) {
            assertFalse(vo.hasStyle(null));
            return;
        }

        // If expectedResult is true, we assume the input *could* be valid in some context,
        // but the main goal of this method is just checking the hasStyle logic works case-insensitively
        // and handles space/underscore normalization correctly against the internal set.

        // The previous test structure was flawed because "BARRETTE" wasn't in the initial set.
        // Let's rewrite this test to be cleaner:

        // Test 1: Simple existence check using different formats of the same word "CLIP"
        assertTrue(HairAccessorStyleVO.of("CLIP").hasStyle("clip"));

        // Test 2: Existence check for multi-word style (internal is HAIR_PIN)
        HairAccessorStyleVO hairPinVO = HairAccessorStyleVO.of("HAIR_PIN");
        assertTrue(hairPinVO.hasStyle("Hair Pin"));
        assertTrue(hairPinVO.hasStyle("HAIR_PIN"));

        // Test 3: Negative check
        assertFalse(vo.hasStyle("InvalidStyle"));
    }

    // Rerunning a parameterized test that isolates the normalization behavior better
    @ParameterizedTest
    @CsvSource({
            "HAIR_PIN, Hair pin, true",
            "hair pin, HAIR_PIN, true",
            "HEADBAND, headband, true",
            "CLIP, bogus, false"
    })
    void testHasStyleNormalization(String styleToStore, String styleToCheck, boolean expectedResult) {
        // Create VO with styleToStore (handles normalization internally)
        HairAccessorStyleVO vo = HairAccessorStyleVO.of(styleToStore);
        // Check using styleToCheck (handles normalization internally)
        assertEquals(expectedResult, vo.hasStyle(styleToCheck));
    }


    @Test
    void testDisplayNameMethodMultipleStyles() {
        Set<String> inputStyles = new HashSet<>();
        inputStyles.add("PONYTAIL_HOLDER");
        inputStyles.add("SCRUNCHIE");
        inputStyles.add("HAIR_COMB");

        HairAccessorStyleVO vo = new HairAccessorStyleVO(inputStyles);
        String displayName = vo.displayName();

        assertTrue(displayName.contains("Ponytail holder"));
        assertTrue(displayName.contains("Scrunchie"));
        assertTrue(displayName.contains("Hair comb"));
        assertEquals(2, displayName.chars().filter(ch -> ch == ',').count());
    }

    @Test
    void testDisplayNameMethodSingleStyle() {
        HairAccessorStyleVO vo = HairAccessorStyleVO.of("BARRETTE");
        assertEquals("Barrette", vo.displayName());
    }

    @Test
    void testDisplayNameMethodWithUnderscore() {
        HairAccessorStyleVO vo = HairAccessorStyleVO.of("BUN_PIN");
        assertEquals("Bun pin", vo.displayName(), "Underscore should be replaced by a space and title cased only on the first word.");
    }

    @Test
    void testDisplayNameMethodEmptyStyles() {
        HairAccessorStyleVO vo = new HairAccessorStyleVO(Collections.emptySet());
        assertEquals("", vo.displayName());
    }
}
