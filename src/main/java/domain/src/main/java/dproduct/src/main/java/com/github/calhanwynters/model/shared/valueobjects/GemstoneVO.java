package com.github.calhanwynters.model.shared.valueobjects;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Domain value object representing a gemstone used in jewelry.
 * - Immutable, domain-only (no infra annotations/deps).
 * - Stores canonical gemstone type, optional quality/grade label, optional carat weight.
 * - Uses nullable fields for optional data; expose Optionals via accessors.
 * - Factories validate invariants and normalize inputs.
 */
public record GemstoneVO(
        String id,
        GemstoneType type,
        String grade,           // nullable, e.g. "VVS1", "SI2", "AAA"
        BigDecimal carat        // nullable, precise weight in carats
) {

    // Public factories
    public static GemstoneVO of(GemstoneType type) {
        return create(UUID.randomUUID().toString(), type, null, null);
    }

    public static GemstoneVO of(GemstoneType type, String grade) {
        return create(UUID.randomUUID().toString(), type, grade, null);
    }

    public static GemstoneVO of(GemstoneType type, BigDecimal carat) {
        return create(UUID.randomUUID().toString(), type, null, carat);
    }

    public static GemstoneVO of(GemstoneType type, String grade, BigDecimal carat) {
        return create(UUID.randomUUID().toString(), type, grade, carat);
    }

    public static GemstoneVO withId(String id, GemstoneType type, String grade, BigDecimal carat) {
        return create(Objects.requireNonNull(id), type, grade, carat);
    }

    // Centralized creation + validation
    public static GemstoneVO create(String id, GemstoneType type, String grade, BigDecimal carat) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(type, "type must not be null");

        String normalizedGrade = normalizeGrade(grade);
        BigDecimal normalizedCarat = normalizeCarat(carat);

        // Example invariant: if type == OTHER, grade or carat not required but allowed
        // Example invariant: carat must be positive if present
        if (normalizedCarat != null && normalizedCarat.signum() <= 0) {
            throw new IllegalArgumentException("carat must be positive if specified");
        }

        return new GemstoneVO(id, type, normalizedGrade, normalizedCarat);
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
        return gradeOptional()
                .map(g -> g + " " + canonicalName())
                .orElse(canonicalName());
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

    // Value semantics ignoring id
    public boolean sameValue(GemstoneVO other) {
        if (other == null) return false;
        return this.type == other.type
                && Objects.equals(normalizeGradeOpt(this.grade), normalizeGradeOpt(other.grade))
                && Objects.equals(this.carat, other.carat);
    }

    // Mutators returning new instances
    public GemstoneVO withGrade(String newGrade) {
        return create(this.id, this.type, newGrade, this.carat);
    }

    public GemstoneVO withCarat(BigDecimal newCarat) {
        return create(this.id, this.type, this.grade, newCarat);
    }

    public GemstoneVO withoutGrade() {
        return create(this.id, this.type, null, this.carat);
    }

    public GemstoneVO withoutCarat() {
        return create(this.id, this.type, this.grade, null);
    }

    // Helpers
    private static String normalizeGrade(String grade) {
        if (grade == null) return null;
        String t = grade.strip();
        return t.isEmpty() ? null : t;
    }

    private static String normalizeGradeOpt(String grade) {
        String n = normalizeGrade(grade);
        return n == null ? null : n.toUpperCase();
    }

    private static BigDecimal normalizeCarat(BigDecimal carat) {
        if (carat == null) return null;
        // normalize scale to e.g. 4 decimal places for carat precision
        return carat.stripTrailingZeros().scale() < 0 ? carat : carat.stripTrailingZeros();
    }

    @Override
    public String toString() {
        return "GemstoneVO[id=" + id + ", type=" + type + (grade != null ? ", grade=" + grade : "") + (carat != null ? ", carat=" + carat : "") + "]";
    }

    // Canonical gemstone types for domain
    public enum GemstoneType {
        DIAMOND,
        SAPPHIRE,
        RUBY,
        EMERALD,
        MOONSTONE,
        OPAL,
        TOPAZ,
        GARNET,
        PERIDOT,
        AQUAMARINE,
        OTHER
    }
}
