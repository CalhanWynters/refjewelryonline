package com.github.calhanwynters.model.shared.valueobjects;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GalleryVOTest {

    @Test
    void constructsWithValidImages() {
        Set<ImageUrlVO> images = Set.of(new ImageUrlVO("http://a.jpg"));
        GalleryVO gallery = new GalleryVO(images);
        assertEquals(1, gallery.images().size());
        assertTrue(gallery.images().contains(new ImageUrlVO("http://a.jpg")));
    }

    @Test
    void nullImagesThrowsNpe() {
        NullPointerException ex = assertThrows(NullPointerException.class, () -> new GalleryVO(null));
        assertTrue(ex.getMessage().contains("Images set cannot be null"));
    }

    @Test
    void emptyImagesThrowsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new GalleryVO(Collections.emptySet()));
        assertTrue(ex.getMessage().contains("must contain at least one"));
    }

    @Test
    void tooManyImagesThrowsIllegalArgumentException() {
        Set<ImageUrlVO> many = new HashSet<>();
        for (int i = 0; i < 16; i++) {
            many.add(new ImageUrlVO("http://img" + i + ".jpg"));
        }
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new GalleryVO(many));
        assertTrue(ex.getMessage().contains("cannot contain more than 15"));
    }

    @Test
    void imagesAreImmutableCopy() {
        Set<ImageUrlVO> mutable = new HashSet<>();
        mutable.add(new ImageUrlVO("http://a.jpg"));
        GalleryVO gallery = new GalleryVO(mutable);
        mutable.add(new ImageUrlVO("http://b.jpg"));
        assertEquals(1, gallery.images().size(), "Gallery should have made an immutable copy of the images set");
    }

    @Test
    void getPrimaryImageReturnsFirstIteratorElement() {
        Set<ImageUrlVO> images = new HashSet<>();
        images.add(new ImageUrlVO("http://first.jpg"));
        images.add(new ImageUrlVO("http://second.jpg"));
        GalleryVO gallery = new GalleryVO(images);
        ImageUrlVO primary = gallery.getPrimaryImage();
        assertNotNull(primary);
        assertTrue(images.contains(primary));
    }
}
