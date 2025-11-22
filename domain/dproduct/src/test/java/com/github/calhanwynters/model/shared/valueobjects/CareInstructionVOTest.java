package com.github.calhanwynters.model.shared.valueobjects;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CareInstructionVOTest {

    @Test
    void constructsAndReturnsDisplay() {
        CareInstructionVO vo = new CareInstructionVO("Hand wash only");
        assertEquals("Hand wash only", vo.display());
        assertEquals("Hand wash only", vo.instructions());
    }

    @Test
    void nullInstructionsThrowsNpe() {
        NullPointerException ex = assertThrows(NullPointerException.class, () -> new CareInstructionVO(null));
        assertTrue(ex.getMessage().contains("Instructions cannot be null"));
    }

    @Test
    void blankInstructionsThrowsIllegalArgumentException() {
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class, () -> new CareInstructionVO(""));
        assertTrue(ex1.getMessage().contains("Instructions cannot be empty or blank"));

        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, () -> new CareInstructionVO("   "));
        assertTrue(ex2.getMessage().contains("Instructions cannot be empty or blank"));
    }
}
