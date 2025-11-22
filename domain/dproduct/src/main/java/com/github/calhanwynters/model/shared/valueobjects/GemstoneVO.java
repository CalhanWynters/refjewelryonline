// This value object needs research and work!!!!!!






package com.github.calhanwynters.model.shared.valueobjects;

import com.github.calhanwynters.model.shared.enums.GemstoneType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.Optional;

/*** Domain value object representing a gemstone used in jewelry.
 * - Immutable, domain-only (no infra annotations/deps).
 * - Stores canonical gemstone type, optional quality/grade label, optional carat weight.
 * - Uses nullable fields for optional data; expose Optionals via accessors.
 * - Factories validate invariants and normalize inputs.*/
public record GemstoneVO(
        GemstoneType type,
        String grade,           // nullable, e.g. "VVS1", "SI2", "AAA"
        BigDecimal carat        // nullable, precise weight in carats
) {

    // Public factories
    public static GemstoneVO of(GemstoneType type) {
        return create(type, null, null);
    }

    public static GemstoneVO of(GemstoneType type, String grade) {
        return create(type, grade, null);
    }

    public static GemstoneVO of(GemstoneType type, BigDecimal carat) {
        return create(type, null, carat);
    }

    public static GemstoneVO of(GemstoneType type, String grade, BigDecimal carat) {
        return create(type, grade, carat);
    }

    // Centralized creation + validation
    public static GemstoneVO create(GemstoneType type, String grade, BigDecimal carat) {
        Objects.requireNonNull(type, "type must not be null");

        String normalizedGrade = normalizeGrade(grade);
        BigDecimal normalizedCarat = normalizeCarat(carat);

        // Invariant: carat must be positive if present
        if (normalizedCarat != null && normalizedCarat.signum() <= 0) {
            throw new IllegalArgumentException("carat must be positive if specified");
        }

        return new GemstoneVO(type, normalizedGrade, normalizedCarat);
    }

    // Optional accessors
    public Optional<String> gradeOptional() {
        return Optional.ofNullable(grade);
    }

    public Optional<BigDecimal> caratOptional() {
        return Optional.ofNullable(carat);
    }

    // Domain behaviors
    public String displayName() {
        return gradeOptional().map(g -> g + " " + canonicalName()).orElse(canonicalName());
    }

    public String canonicalName() {
        return switch (type) {
            case DIAMOND -> "Diamond";
            case SAPPHIRE -> "Sapphire";
            case RUBY -> "Ruby";
            case EMERALD -> "Emerald";
            case MOONSTONE -> "Moonstone";
            case OPAL -> "Opal";
            case TOPAZ -> "Topaz";
            case GARNET -> "Garnet";
            case PERIDOT -> "Peridot";
            case AQUAMARINE -> "Aquamarine";
            case OTHER -> "Other";
        };
    }

    // Value semantics are handled automatically by the Java record type.

    // Mutators returning new instances
    public GemstoneVO withGrade(String newGrade) {
        return create(this.type, newGrade, this.carat);
    }

    public GemstoneVO withCarat(BigDecimal newCarat) {
        return create(this.type, this.grade, newCarat);
    }

    public GemstoneVO withoutGrade() {
        return create(this.type, null, this.carat);
    }

    public GemstoneVO withoutCarat() {
        return create(this.type, this.grade, null);
    }

    // Helpers
    private static String normalizeGrade(String grade) {
        if (grade == null) return null;
        String t = grade.strip();
        return t.isEmpty() ? null : t;
    }

    /*
     * Normalizes carat weight:
     * 1. Strips trailing zeros.
     * 2. Sets consistent scale of 4 decimal places for business precision.
     */
    private static BigDecimal normalizeCarat(BigDecimal carat) {
        if (carat == null) return null;
        BigDecimal stripped = carat.stripTrailingZeros();
        // Enforce consistent scale for domain precision
        return stripped.setScale(4, RoundingMode.HALF_UP);
    }
}
