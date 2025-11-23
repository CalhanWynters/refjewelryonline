package com.github.calhanwynters.model.shared.valueobjects;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Persistable-friendly immutable record for gemstone type.
 *
 * <p>Invariants enforced:
 * <ul>
 *   <li><b>id</b> may be null for transient instances; when non-null it must be positive (> 0).</li>
 *   <li><b>name</b> is required, trimmed, non-blank, and must match a conservative gemstone-name pattern
 *       (letters, spaces, hyphens, apostrophes). Adjust the pattern for your domain if needed.</li>
 *   <li><b>description</b> is optional; when provided it is trimmed and must not exceed 1000 characters
 *       (adjust the limit to suit your application's constraints).</li>
 * </ul>
 *
 * <p>Equality and hashing:
 * <ul>
 *   <li>{@link #equals(Object)}: If both instances have non-null ids, equality is based solely on id equality.
 *       Otherwise equality falls back to a case-insensitive comparison of the name.</li>
 *   <li>{@link #hashCode()}: When id is non-null the hash is derived from the id; otherwise it is derived
 *       from the lower-cased name to remain consistent with the equals() behavior when id is absent.</li>
 * </ul>
 *
 * @param id          nullable DB id (when non-null must be positive)
 * @param name        required, e.g. "Diamond"
 * @param description nullable; trimmed; max length 1000
 */
public record GemstoneTypeVO(Long id, String name, String description) {

    private static final int DESCRIPTION_MAX_LENGTH = 1000;
    // conservative pattern: letters, spaces, hyphens, apostrophes; tweak as needed
    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L}0-9'\\- ]+$");

    // Compact constructor enforces invariants on all construction paths
    public GemstoneTypeVO {
        // id: nullable but if present must be positive
        if (id != null && id <= 0) {
            throw new IllegalArgumentException("id must be positive when provided");
        }

        Objects.requireNonNull(name, "name must not be null");
        String n = name.strip();
        if (n.isEmpty()) throw new IllegalArgumentException("name must not be blank");
        if (!NAME_PATTERN.matcher(n).matches()) {
            throw new IllegalArgumentException("name contains invalid characters");
        }
        name = n;

        if (description != null) {
            String d = description.strip();
            if (d.length() > DESCRIPTION_MAX_LENGTH) {
                throw new IllegalArgumentException("description must not exceed " + DESCRIPTION_MAX_LENGTH + " characters");
            }
            description = d;
        }
    }

    // Factory for new/transient types (no id)
    public static GemstoneTypeVO of(String name) {
        return new GemstoneTypeVO(null, name, null);
    }

    public static GemstoneTypeVO of(String name, String description) {
        return new GemstoneTypeVO(null, name, description);
    }

    // Factory with id (for reconstituted / persisted instances)
    public static GemstoneTypeVO of(Long id, String name, String description) {
        return new GemstoneTypeVO(id, name, description);
    }

    /**
     * Equality defined as:
     * - If both instances have non-null ids, equality is based solely on id equality.
     * - Otherwise equality falls back to a case-insensitive comparison of the name.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GemstoneTypeVO that)) return false;
        if (this.id != null && that.id != null) return this.id.equals(that.id);
        return this.name.equalsIgnoreCase(that.name);
    }

    /**
     * Hash code consistent with equals():
     * - When id is non-null, hash is derived from id.
     * - Otherwise hash is derived from the lower-cased name.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id != null ? id : name.toLowerCase());
    }

    @Override
    public String toString() {
        return "GemstoneTypeVO{id=" + id + ", name='" + name + "'}";
    }
}
