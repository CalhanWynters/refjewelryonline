package com.github.calhanwynters.model.ankletattributes;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Domain value object representing the style attributes of an anklet.
 * - Immutable record, domain-only (no infra annotations/deps).
 * - Stores style names as a set (e.g., "Beaded", "Chain").
 * - Ensures valid styles are used and provides display functionality.
 */
public record AnkletStyleVO(
        Set<String> styles // Set of validated, normalized style names (non-null, immutable)
) {
    // A centralized source of truth for all valid styles in the domain
    private static final Set<String> VALID_STYLES;

    static {
        Set<String> s = new HashSet<>();
        s.add("BEADED");
        s.add("CHAIN");
        s.add("CHARM");
        s.add("LINK");
        s.add("CUFF");
        s.add("GEMSTONE");
        s.add("LAYERED");
        s.add("PEARL");
        s.add("ROPE");
        s.add("SLIDER");
        VALID_STYLES = Collections.unmodifiableSet(s);
    }

    // Compact constructor with validation and normalization
    public AnkletStyleVO {
        Objects.requireNonNull(styles, "styles set must not be null");

        // Normalize and validate all styles in the input set
        Set<String> normalizedAndValidated = styles.stream()
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

        // Replace the input set with the normalized, immutable set
        styles = normalizedAndValidated;
    }

    // --- Factories ---

    /**
     * Creates an AnkletStyleVO from a single style string.
     * @param style The single style name.
     * @return A new AnkletStyleVO instance.
     */
    public static AnkletStyleVO of(String style) {
        return new AnkletStyleVO(Set.of(style));
    }

    /**
     * Creates an AnkletStyleVO from multiple style strings.
     * @param styles The set of style names.
     * @return A new AnkletStyleVO instance.
     */
    public static AnkletStyleVO of(Set<String> styles) {
        return new AnkletStyleVO(styles);
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
                .map(style -> style.charAt(0) + style.substring(1).toLowerCase()) // Convert "BEADED" to "Beaded"
                .collect(Collectors.joining(", "));
    }
}
