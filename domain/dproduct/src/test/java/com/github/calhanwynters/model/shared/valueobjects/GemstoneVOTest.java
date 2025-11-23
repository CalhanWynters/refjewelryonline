package com.github.calhanwynters.model.shared.valueobjects;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

class GemstoneVOTest {

    @Test
    void factoryDefaultsBooleansToFalseAndAcceptsType() {
        var type = GemstoneTypeVO.of("Diamond");
        var g = GemstoneVO.of(type);

        assertNotNull(g);
        assertSame(type, g.type());
        assertFalse(g.hasCertificate());
        assertFalse(g.isLabGrown());
        assertTrue(g.gradeOptional().isEmpty());
        assertTrue(g.caratOptional().isEmpty());
    }

    @Test
    void createNormalizesGradeAndCaratAndPreservesFlags() {
        var type = GemstoneTypeVO.of("Ruby");
        var inputCarat = new BigDecimal("1.2300");
        var g = GemstoneVO.create(type, " VVS1 ", inputCarat, true, true);

        assertEquals("VVS1", g.grade());
        assertTrue(g.caratOptional().isPresent());
        assertEquals(inputCarat.setScale(4, RoundingMode.HALF_UP), g.carat());
        assertTrue(g.hasCertificate());
        assertTrue(g.isLabGrown());
    }

    @Test
    void normalizeBlankGradeBecomesNull() {
        var type = GemstoneTypeVO.of("Sapphire");
        var g = GemstoneVO.of(type, "   ");

        assertTrue(g.gradeOptional().isEmpty());
    }

    @Test
    void withModifiersReturnNewInstancesWithUpdatedValues() {
        var type = GemstoneTypeVO.of("Emerald");
        var base = GemstoneVO.create(type, "SI2", new BigDecimal("0.50"), false, false);

        var withGrade = base.withGrade("VVS2");
        assertNotSame(base, withGrade);
        assertEquals("VVS2", withGrade.grade());
        assertEquals(base.caratOptional(), withGrade.caratOptional());
        assertEquals(base.hasCertificate(), withGrade.hasCertificate());
        assertEquals(base.isLabGrown(), withGrade.isLabGrown());

        var withCarat = base.withCarat(new BigDecimal("0.7500"));
        assertEquals(new BigDecimal("0.7500").setScale(4, RoundingMode.HALF_UP), withCarat.carat());

        var withCert = base.withCertificate(true);
        assertTrue(withCert.hasCertificate());

        var withLab = base.withLabGrown(true);
        assertTrue(withLab.isLabGrown());
    }

    @Test
    void withoutModifiersClearFields() {
        var type = GemstoneTypeVO.of("Opal");
        var base = GemstoneVO.create(type, "AAA", new BigDecimal("2.0000"), true, true);

        var noGrade = base.withoutGrade();
        assertTrue(noGrade.gradeOptional().isEmpty());
        assertEquals(base.caratOptional(), noGrade.caratOptional());

        var noCarat = base.withoutCarat();
        assertTrue(noCarat.caratOptional().isEmpty());
        assertEquals(base.gradeOptional(), noCarat.gradeOptional());
    }

    @Test
    void displayNameUsesGradeWhenPresentOtherwiseTypeName() {
        var type = GemstoneTypeVO.of("Peridot");
        var a = GemstoneVO.create(type, "AAA", null, false, false);
        assertEquals("AAA Peridot", a.displayName());

        var b = GemstoneVO.of(type);
        assertEquals("Peridot", b.displayName());
    }

    @Test
    void caratMustBePositiveIfPresent() {
        var type = GemstoneTypeVO.of("Garnet");
        assertThrows(IllegalArgumentException.class, () -> GemstoneVO.create(type, null, new BigDecimal("-0.1"), false, false));
        assertThrows(IllegalArgumentException.class, () -> GemstoneVO.create(type, null, BigDecimal.ZERO, false, false));
    }

    @Test
    void typeCannotBeNull() {
        assertThrows(NullPointerException.class, () -> GemstoneVO.of(null));
        assertThrows(NullPointerException.class, () -> GemstoneVO.create(null, "VVS1", new BigDecimal("1.0"), false, false));
    }
}
