package com.github.calhanwynters.model.shared.valueobjects;

import java.util.Objects;
import java.util.Set;

public record GalleryVO(Set<ImageUrlVO> images) {
    public GalleryVO {
        Objects.requireNonNull(images, "Images set cannot be null");
        if (images.isEmpty()) {
            throw new IllegalArgumentException("A gallery must contain at least one image.");
        }
        if (images.size() > 15) {
            throw new IllegalArgumentException("A gallery cannot contain more than 15 images.");
        }
        images = Set.copyOf(images);
    }

    // Example behavior: get the primary image (assuming the first is primary if using a List)
    public ImageUrlVO getPrimaryImage() {
        if (images.isEmpty()) return null; // Or throw exception based on domain rules
        return images.iterator().next();
    }
}
