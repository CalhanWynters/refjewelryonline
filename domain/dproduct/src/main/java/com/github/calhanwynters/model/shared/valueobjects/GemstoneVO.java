package com.github.calhanwynters.model.shared.valueobjects;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.Optional;

/**
 * Domain value object representing a gemstone used in jewelry.
 *
 * @param type            required GemstoneTypeVO
 * @param grade           nullable, e.g. "VVS1"
 * @param carat           nullable BigDecimal weight in carats
 * @param hasCertificate  whether the gemstone has a certificate (true/false)
 * @param isLabGrown      whether the gemstone is lab-grown (true/false)
 */
public record GemstoneVO(
        GemstoneTypeVO type,
        String grade,
        BigDecimal carat,
        boolean hasCertificate,
        boolean isLabGrown
) {

    // Compact constructor to ensure type non-null for all creation paths
    public GemstoneVO {
        Objects.requireNonNull(type, "type must not be null");
    }

    public static GemstoneVO of(GemstoneTypeVO type) {
        return create(type, null, null, false, false);
    }

    public static GemstoneVO of(GemstoneTypeVO type, String grade) {
        return create(type, grade, null, false, false);
    }

    public static GemstoneVO of(GemstoneTypeVO type, BigDecimal carat) {
        return create(type, null, carat, false, false);
    }

    public static GemstoneVO of(GemstoneTypeVO type, String grade, BigDecimal carat) {
        return create(type, grade, carat, false, false);
    }

    // New factory overloads including certificate and lab-grown flags
    public static GemstoneVO of(GemstoneTypeVO type, String grade, BigDecimal carat, boolean hasCertificate) {
        return create(type, grade, carat, hasCertificate, false);
    }

    public static GemstoneVO of(GemstoneTypeVO type, String grade, BigDecimal carat, boolean hasCertificate, boolean isLabGrown) {
        return create(type, grade, carat, hasCertificate, isLabGrown);
    }

    public static GemstoneVO create(GemstoneTypeVO type, String grade, BigDecimal carat, boolean hasCertificate, boolean isLabGrown) {
        Objects.requireNonNull(type, "type must not be null");

        String normalizedGrade = normalizeGrade(grade);
        BigDecimal normalizedCarat = normalizeCarat(carat);

        if (normalizedCarat != null && normalizedCarat.signum() <= 0) {
            throw new IllegalArgumentException("carat must be positive if specified");
        }

        return new GemstoneVO(type, normalizedGrade, normalizedCarat, hasCertificate, isLabGrown);
    }

    public Optional<String> gradeOptional() {
        return Optional.ofNullable(grade);
    }

    public Optional<BigDecimal> caratOptional() {
        return Optional.ofNullable(carat);
    }

    public boolean hasCertificate() {
        return hasCertificate;
    }

    public boolean isLabGrown() {
        return isLabGrown;
    }

    public String displayName() {
        return gradeOptional().map(g -> g + " " + type.name()).orElse(type.name());
    }

    public GemstoneVO withGrade(String newGrade) {
        return create(this.type, newGrade, this.carat, this.hasCertificate, this.isLabGrown);
    }

    public GemstoneVO withCarat(BigDecimal newCarat) {
        return create(this.type, this.grade, newCarat, this.hasCertificate, this.isLabGrown);
    }

    public GemstoneVO withCertificate(boolean hasCertificate) {
        return create(this.type, this.grade, this.carat, hasCertificate, this.isLabGrown);
    }

    public GemstoneVO withLabGrown(boolean isLabGrown) {
        return create(this.type, this.grade, this.carat, this.hasCertificate, isLabGrown);
    }

    public GemstoneVO withoutGrade() {
        return create(this.type, null, this.carat, this.hasCertificate, this.isLabGrown);
    }

    public GemstoneVO withoutCarat() {
        return create(this.type, this.grade, null, this.hasCertificate, this.isLabGrown);
    }

    private static String normalizeGrade(String grade) {
        if (grade == null) return null;
        String t = grade.strip();
        return t.isEmpty() ? null : t;
    }

    /*
     * Normalizes carat weight:
     * - stripTrailingZeros then ensure scale is 4 (business precision).
     */
    private static BigDecimal normalizeCarat(BigDecimal carat) {
        if (carat == null) return null;
        BigDecimal stripped = carat.stripTrailingZeros();
        int targetScale = 4;
        return stripped.setScale(targetScale, RoundingMode.HALF_UP);
    }
}
