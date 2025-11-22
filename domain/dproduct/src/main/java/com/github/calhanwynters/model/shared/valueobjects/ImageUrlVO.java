package com.github.calhanwynters.model.shared.valueobjects;

import java.util.Objects;

public record ImageUrlVO(String url) {
    public ImageUrlVO {
        Objects.requireNonNull(url, "URL cannot be null");
        if (!url.startsWith("http")) { // Basic validation
            throw new IllegalArgumentException("Invalid URL format");
        }
    }
}
