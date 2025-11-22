package com.github.calhanwynters.model.ringattributes;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Domain value object representing the style or setting attributes of a ring.
 * - Immutable record, domain-only (no infra annotations/deps).
 * - Stores style names as a set (e.g., "Solitaire", "Halo", "Vintage").
 * - Ensures valid styles are used and provides display functionality.
 */
public record RingStyleVO(
        Set<String> styles // Set of validated, normalized style names (non-null, immutable)
) {
    // A centralized source of truth for all valid styles in the domain
    private static final Set<String> VALID_STYLES;

    static {
        VALID_STYLES = Set.of("SOLITAIRE", "HALO", "PAVE", "CHANNEL_SET", "PRONG_SET", "BEZEL_SET", "VINTAGE", "MODERN", "CLASSIC", "BAND", "STACKABLE");
    }

    // Compact constructor with validation and normalization
    public RingStyleVO {
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
     * Creates a RingStyleVO from a single style string.
     * @param style The single style name.
     * @return A new RingStyleVO instance.
     */
    public static RingStyleVO of(String style) {
        return new RingStyleVO(Set.of(style));
    }

    /**
     * Creates a RingStyleVO from multiple style strings.
     * @param styles The set of style names.
     * @return A new RingStyleVO instance.
     */
    public static RingStyleVO of(Set<String> styles) {
        return new RingStyleVO(styles);
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
                    // Convert "CHANNEL_SET" to "Channel Set"
                    String displayName = style.replace("_", " ");
                    // Convert to Title Case
                    return displayName.charAt(0) + displayName.substring(1).toLowerCase();
                })
                .collect(Collectors.joining(", "));
    }
}
