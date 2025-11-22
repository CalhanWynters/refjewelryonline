package com.github.calhanwynters.model.shared.valueobjects;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PercentageVOTest {

    @Test
    void constructsWithValidValue() {
        PercentageVO percentage = new PercentageVO(BigDecimal.valueOf(0.75));
        assertEquals(0.75, percentage.value().doubleValue(), 0.0001);
        assertEquals(4, percentage.value().scale());
    }

    @Test
    void constructsWithZeroValue() {
        PercentageVO percentage = new PercentageVO(BigDecimal.ZERO);
        assertEquals(0.0, percentage.value().doubleValue(), 0.0001);
    }

    @Test
    void constructsWithOneValue() {
        PercentageVO percentage = new PercentageVO(BigDecimal.ONE);
        assertEquals(1.0, percentage.value().doubleValue(), 0.0001);
    }

    @Test
    void nullValueThrowsNpe() {
        NullPointerException ex = assertThrows(NullPointerException.class, () -> new PercentageVO(null));
        assertTrue(ex.getMessage().contains("value must not be null"));
    }

    @Test
    void negativeValueThrowsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new PercentageVO(BigDecimal.valueOf(-0.1)));
        assertTrue(ex.getMessage().contains("Percentage must be between 0 and 1"));
    }

    @Test
    void aboveOneThrowsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new PercentageVO(BigDecimal.valueOf(1.1)));
        assertTrue(ex.getMessage().contains("Percentage must be between 0 and 1"));
    }

    @Test
    void constructsWithHighPrecisionValue() {
        PercentageVO percentage = new PercentageVO(new BigDecimal("0.123456789"));
        assertEquals(new BigDecimal("0.1235"), percentage.value());
    }

    @Test
    void constructsWithOneDecimalScale() {
        PercentageVO percentage = new PercentageVO(new BigDecimal("0.5"));
        assertEquals(new BigDecimal("0.5000"), percentage.value());
    }
}
