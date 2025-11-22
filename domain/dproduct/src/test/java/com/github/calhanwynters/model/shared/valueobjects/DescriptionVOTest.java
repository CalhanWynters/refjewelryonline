package com.github.calhanwynters.model.shared.valueobjects;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DescriptionVOTest {

    @Test
    void constructsWithValidValue() {
        String text = "This is a valid product description.";
        DescriptionVO vo = new DescriptionVO(text);
        assertEquals(text.strip(), vo.value());
    }

    @Test
    void ofFactoryCreatesInstance() {
        DescriptionVO vo = DescriptionVO.of("          A nicely trimmed description.  ");
        assertEquals("A nicely trimmed description.", vo.value());
    }

    @Test
    void nullValueThrowsNpe() {
        NullPointerException ex = assertThrows(NullPointerException.class, () -> new DescriptionVO(null));
        assertTrue(ex.getMessage().contains("Description value cannot be null"));
    }

    @Test
    void tooShortThrowsIllegalArgumentException() {
        String tooShort = "short";
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new DescriptionVO(tooShort));
        assertTrue(ex.getMessage().contains("at least"));
    }

    @Test
    void tooLongThrowsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new DescriptionVO("x".repeat(2010)));
        assertTrue(ex.getMessage().contains("cannot exceed"));
    }

    @Test
    void truncateShorterThanMaxReturnsSameInstance() {
        DescriptionVO vo = new DescriptionVO("This description is fine.");
        DescriptionVO truncated = vo.truncate(100);
        assertSame(vo, truncated);
    }

    @Test
    void truncateLongerProducesEllipsizedValue() {
        DescriptionVO vo = new DescriptionVO("a".repeat(100));
        DescriptionVO t = vo.truncate(10);
        assertEquals(10, t.value().length());
        assertTrue(t.value().endsWith("..."));
    }
}
