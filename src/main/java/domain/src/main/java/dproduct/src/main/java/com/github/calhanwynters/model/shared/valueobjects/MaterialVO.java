package com.github.calhanwynters.model.shared.valueobjects;

import java.util.Objects;
import java.util.Optional;

/*** Domain value object representing a material used in jewelry or manufacturing.
 * - Immutable, domain-only (no infra annotations/deps).
 * - Stores canonical material name and optional label.
 * - Uses nullable field for optional data; exposes Optional via accessor.
 * - Validation is handled in the compact constructor.*/
public record MaterialVO(
        MaterialName material,
        String label // nullable; callers should use labelOptional()
) {
    // Compact constructor with validation and normalization
    public MaterialVO {
        Objects.requireNonNull(material, "material must not be null");

        String normalized = normalizeLabel(label);

        if (material == MaterialName.OTHER && normalized.isEmpty()) {
            throw new IllegalArgumentException("label is required when material is OTHER");
        }

        // Ensure the internal field 'label' is normalized (null if empty/whitespace only)
        label = normalized.isEmpty() ? null : normalized;
    }

    // Public factories
    public static MaterialVO of(MaterialName material) {
        return new MaterialVO(material, null);
    }

    public static MaterialVO of(MaterialName material, String label) {
        return new MaterialVO(material, label);
    }

    // Optional accessor
    public Optional<String> labelOptional() {
        return Optional.ofNullable(label);
    }

    // Domain behaviors
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

    // Mutators returning new instances
    public MaterialVO withLabel(String newLabel) {
        return new MaterialVO(this.material, newLabel);
    }

    public MaterialVO withoutLabel() {
        return new MaterialVO(this.material, null);
    }

    // Helpers
    private static String normalizeLabel(String label) {
        if (label == null) return "";
        String t = label.strip();
        return t.isEmpty() ? "" : t;
    }

    // Canonical material types for domain
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
