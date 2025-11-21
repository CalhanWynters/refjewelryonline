package com.github.calhanwynters.model.necklaceattributes;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Domain value object representing the style attributes of a necklace.
 * - Immutable record, domain-only (no infra annotations/deps).
 * - Stores style names as a set (e.g., "Pendant", "Chain", "Beaded").
 * - Ensures valid styles are used and provides display functionality.
 */
public record NecklaceStyleVO(
        Set<String> styles // Set of validated, normalized style names (non-null, immutable)
) {
    // A centralized source of truth for all valid styles in the domain
    private static final Set<String> VALID_STYLES;

    static {
        VALID_STYLES = Set.of("PENDANT", "CHAIN", "BEADED", "CHOKER", "LARIAT", "OPERA", "RIVIERA", "LAYERED", "COLLAR", "PEARL");
    }

    // Compact constructor with validation and normalization
    public NecklaceStyleVO {
        Objects.requireNonNull(styles, "styles set must not be null");

        // Normalize and validate all styles in the input set

        // Replace the input set with the normalized, immutable set
        styles = styles.stream()
                .filter(Objects::nonNull)
                .map(String::strip)
                .map(String::toUpperCase)
                .filter(s -> !s.isEmpty())
                .peek(style -> {
                    if (!VALID_STYLES.contains(style)) {
                        throw new IllegalArgumentException("Invalid style encountered: " + style);
                    }
                })
                .collect(Collectors.toUnmodifiableSet());
    }

    // --- Factories ---

    /**
     * Creates a NecklaceStyleVO from a single style string.
     * @param style The single style name.
     * @return A new NecklaceStyleVO instance.
     */
    public static NecklaceStyleVO of(String style) {
        return new NecklaceStyleVO(Set.of(style));
    }

    /**
     * Creates a NecklaceStyleVO from multiple style strings.
     * @param styles The set of style names.
     * @return A new NecklaceStyleVO instance.
     */
    public static NecklaceStyleVO of(Set<String> styles) {
        return new NecklaceStyleVO(styles);
    }

    // --- Domain Behaviors ---

    /**
     * Checks if this style object contains a specific style.
     * @param style The style to check for.
     * @return true if the style is present (case-insensitive).
     */
    public boolean hasStyle(String style) {
        if (style == null || style.isBlank()) return false;
        return this.styles.contains(style.strip().toUpperCase());
    }

    /**
     * Returns a human-readable, comma-separated display name of the styles.
     * @return A formatted string of styles.
     */
    public String displayName() {
        return this.styles.stream()
                .map(style -> {
                    // Convert "BEADED" to "Beaded", "RIVIERA" to "Riviera"
                    String displayName = style.replace("_", " ");
                    return displayName.charAt(0) + displayName.substring(1).toLowerCase();
                })
                .collect(Collectors.joining(", "));
    }
}
