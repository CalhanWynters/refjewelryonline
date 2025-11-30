package com.github.calhanwynters.model.earringattributes;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EarringStyleVOTest {

    // --- Constructor and Validation Tests ---

    @Test
    void testConstructorThrowsForNullStylesSet() {
        assertThrows(NullPointerException.class, () -> new EarringStyleVO(null));
    }

    @Test
    void testConstructorThrowsForInvalidStyle() {
        Set<String> invalidSet = new HashSet<>();
        invalidSet.add("Hoop"); // Valid style, different case
        invalidSet.add("InvalidStyleName"); // Invalid style

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> new EarringStyleVO(invalidSet));
        assertTrue(thrown.getMessage().contains("Invalid style encountered: INVALIDSTYLENAME"));
    }

    @Test
    void testConstructorNormalizesAndValidates() {
        Set<String> inputStyles = new HashSet<>();
        inputStyles.add("  hOoP  "); // Test stripping and casing
        inputStyles.add("Stud");     // Test casing
        inputStyles.add(null);      // Test null filtering

        EarringStyleVO vo = new EarringStyleVO(inputStyles);
        Set<String> expectedNormalized = Set.of("HOOP", "STUD");

        assertEquals(expectedNormalized, vo.styles(), "Styles should be normalized to uppercase and stored as an immutable set.");
    }

    @Test
    void testConstructorCreatesImmutableInternalSet() {
        HashSet<String> mutableInputSet = new HashSet<>();
        mutableInputSet.add("DANGLE");

        EarringStyleVO vo = new EarringStyleVO(mutableInputSet);

        // Try to modify the set retrieved via the accessor (should fail)
        assertThrows(UnsupportedOperationException.class, () -> vo.styles().add("STUD"));
    }

    @Test
    void testConstructorHandlesEmptyStringsInInput() {
        Set<String> inputStyles = Set.of("DROP", " ", "");
        EarringStyleVO vo = new EarringStyleVO(inputStyles);
        Set<String> expected = Set.of("DROP");
        assertEquals(expected, vo.styles());
    }

    // --- Factory Method Tests ---

    @Test
    void testOfSingleStyleFactory() {
        EarringStyleVO vo = EarringStyleVO.of("Hoop");
        assertTrue(vo.hasStyle("HOOP"));
        assertEquals(1, vo.styles().size());
    }

    @Test
    void testOfSetFactory() {
        Set<String> inputStyles = Set.of("Stud", "Dangle");
        EarringStyleVO vo = EarringStyleVO.of(inputStyles);
        assertTrue(vo.hasStyle("STUD"));
        assertTrue(vo.hasStyle("DANGLE"));
        assertEquals(2, vo.styles().size());
    }

    // --- Behavior Method Tests ---

    @ParameterizedTest
    @CsvSource({
            "STUD, true",
            "stud, true",
            " STUD , true",
            "HOOP, true",
            "EAR_CUFF, true",
            "Drop, true",
            "invalid, false",
            "null, false"
    })
    void testHasStyleMethod(String styleToCheck, boolean expectedResult) {
        // We initialize a VO with multiple styles to test hasStyle robustly
        EarringStyleVO vo = new EarringStyleVO(Set.of("STUD", "HOOP", "DROP", "EAR_CUFF"));

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
        inputStyles.add("DANGLE");
        inputStyles.add("EAR_CUFF");
        inputStyles.add("STUD");

        EarringStyleVO vo = new EarringStyleVO(inputStyles);
        String displayName = vo.displayName();

        // The order might vary in a Set, so check expected components and general format
        assertTrue(displayName.contains("Dangle"), "Should contain 'Dangle'");
        // Updated expectation to match 'Ear cuff'
        assertTrue(displayName.contains("Ear cuff"), "Should contain 'Ear cuff' (with space and lowercase 'c')");
        assertTrue(displayName.contains("Stud"), "Should contain 'Stud'");
        // Ensure commas separate exactly 3 items
        assertEquals(2, displayName.chars().filter(ch -> ch == ',').count(), "Should have exactly two commas for three items");
    }

    @Test
    void testDisplayNameMethodSingleStyle() {
        EarringStyleVO vo = EarringStyleVO.of("CHANDELIER");
        assertEquals("Chandelier", vo.displayName());
    }

    @Test
    void testDisplayNameMethodWithUnderscore() {
        EarringStyleVO vo = EarringStyleVO.of("EAR_CUFF");
        // Updated expectation to match 'Ear cuff'
        assertEquals("Ear cuff", vo.displayName(), "Underscore should be replaced by a space and title cased only on the first word.");
    }

    @Test
    void testDisplayNameMethodEmptyStyles() {
        EarringStyleVO vo = new EarringStyleVO(Collections.emptySet());
        assertEquals("", vo.displayName());
    }
}
