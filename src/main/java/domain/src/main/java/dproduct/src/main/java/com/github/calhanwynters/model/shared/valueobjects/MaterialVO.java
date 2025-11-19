package com.github.calhanwynters.model.shared.valueobjects;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public record MaterialVO(
        String id,
        MaterialName material,
        String label // nullable; callers should use labelOptional()
) {
    public static MaterialVO of(MaterialName material) {
        return create(UUID.randomUUID().toString(), material, null);
    }

    public static MaterialVO of(MaterialName material, String label) {
        return create(UUID.randomUUID().toString(), material, label);
    }

    public static MaterialVO withId(String id, MaterialName material, String label) {
        return create(Objects.requireNonNull(id), material, label);
    }

    public static MaterialVO create(String id, MaterialName material, String label) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(material, "material must not be null");
        String normalized = normalizeLabel(label);
        if (material == MaterialName.OTHER && normalized.isEmpty()) {
            throw new IllegalArgumentException("label is required when material is OTHER");
        }
        return new MaterialVO(id, material, normalized.isEmpty() ? null : normalized);
    }

    public Optional<String> labelOptional() {
        return Optional.ofNullable(label);
    }

    public boolean isPrecious() {
        return switch (material) {
            case GOLD, WHITE_GOLD, ROSE_GOLD, PLATINUM, PALLADIUM, SILVER -> true;
            default -> false;
        };
    }

    public String displayName() {
        return labelOptional().orElseGet(this::canonicalName);
    }

    public String canonicalName() {
        return switch (material) {
            case GOLD -> "Gold";
            case WHITE_GOLD -> "White Gold";
            case ROSE_GOLD -> "Rose Gold";
            case PLATINUM -> "Platinum";
            case PALLADIUM -> "Palladium";
            case SILVER -> "Silver";
            case BRONZE -> "Bronze";
            case COPPER -> "Copper";
            case STAINLESS_STEEL -> "Stainless Steel";
            case TITANIUM -> "Titanium";
            case TUNGSTEN -> "Tungsten";
            case RHODIUM_PLATING -> "Rhodium Plating";
            case OTHER -> "Other";
        };
    }

    public boolean sameValue(MaterialVO other) {
        if (other == null) return false;
        return this.material == other.material
                && Objects.equals(normalizeLabelOpt(this.label), normalizeLabelOpt(other.label));
    }

    public MaterialVO withLabel(String newLabel) {
        return create(this.id, this.material, newLabel);
    }

    public MaterialVO withoutLabel() {
        return create(this.id, this.material, null);
    }

    private static String normalizeLabel(String label) {
        if (label == null) return "";
        String t = label.strip();
        return t.isEmpty() ? "" : t;
    }

    private static String normalizeLabelOpt(String label) {
        String n = normalizeLabel(label);
        return n.isEmpty() ? null : n.toLowerCase();
    }

    @Override
    public String toString() {  // Not annotated method overrides method annotated with @NotNull
        return "MaterialVO[id=" + id + ", material=" + material + labelOptional().map(l -> ", label=" + l).orElse("") + "]";
    }

    public enum MaterialName {
        GOLD,
        WHITE_GOLD,
        ROSE_GOLD,
        PLATINUM,
        PALLADIUM,
        SILVER,
        BRONZE,
        COPPER,
        STAINLESS_STEEL,
        TITANIUM,
        TUNGSTEN,
        RHODIUM_PLATING,
        OTHER
    }
}
