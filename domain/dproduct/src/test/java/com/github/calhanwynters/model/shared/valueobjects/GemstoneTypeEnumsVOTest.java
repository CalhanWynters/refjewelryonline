package com.github.calhanwynters.model.shared.valueobjects;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class GemstoneTypeEnumsVOTest {

    @Test
    void factoryCreatesValidInstance() {
        var g = GemstoneTypeVO.of(" Diamond ");
        assertThat(g.id()).isNull();
        assertThat(g.name()).isEqualTo("Diamond"); // trimmed
        assertThat(g.description()).isNull();
    }

    @Test
    void factoryWithDescriptionCreatesValidInstance() {
        var g = GemstoneTypeVO.of("Ruby", "  deep red  ");
        assertThat(g.id()).isNull();
        assertThat(g.name()).isEqualTo("Ruby");
        assertThat(g.description()).isEqualTo("deep red");
    }

    @Test
    void factoryWithIdReconstitutesInstance() {
        var g = GemstoneTypeVO.of(42L, "Emerald", "green");
        assertThat(g.id()).isEqualTo(42L);
        assertThat(g.name()).isEqualTo("Emerald");
        assertThat(g.description()).isEqualTo("green");
    }

    @Test
    void compactConstructorRejectsNullOrBlankName() {
        assertThatThrownBy(() -> new GemstoneTypeVO(null, null, null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new GemstoneTypeVO(null, "   ", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void compactConstructorRejectsInvalidNameCharacters() {
        assertThatThrownBy(() -> new GemstoneTypeVO(1L, "Invalid@Name", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("contains invalid characters");
    }

    @Test
    void compactConstructorRejectsNegativeOrZeroId() {
        assertThatThrownBy(() -> new GemstoneTypeVO(0L, "Diamond", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must be positive");
        assertThatThrownBy(() -> new GemstoneTypeVO(-1L, "Diamond", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must be positive");
    }

    @Test
    void equalsUsesIdWhenBothPresent() {
        var a = GemstoneTypeVO.of(1L, "Diamond", null);
        var b = GemstoneTypeVO.of(1L, "SomeOther", null);
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    void equalsFallsBackToNameWhenIdMissing() {
        var a = GemstoneTypeVO.of("Diamond");
        var b = GemstoneTypeVO.of(" diamond ");
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    void persistedAndTransientCompareByNameWhenOneIdMissing() {
        var persisted = GemstoneTypeVO.of(5L, "Sapphire", null);
        var transientSameName = GemstoneTypeVO.of("sapphire");
        // Since one id is null, equality falls back to comparing names (case-insensitive)
        assertThat(persisted).isEqualTo(transientSameName);
    }

    @Test
    void differentPersistedIdsAreNotEqualEvenIfNamesMatch() {
        var a = GemstoneTypeVO.of(1L, "Topaz", null);
        var b = GemstoneTypeVO.of(2L, "Topaz", null);
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void toStringContainsIdAndName() {
        var g = GemstoneTypeVO.of(7L, "Opal", null);
        assertThat(g.toString()).contains("id=7").contains("name='Opal'");
    }
}
