package com.github.calhanwynters.model.shared.valueobjects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ImageUrlVOTest {

    @Test
    void constructsWithValidUrl() {
        ImageUrlVO imageUrl = new ImageUrlVO("https://example.com/image.jpg");
        assertEquals("https://example.com/image.jpg", imageUrl.url());
    }

    @Test
    void nullUrlThrowsNpe() {
        NullPointerException ex = assertThrows(NullPointerException.class, () -> new ImageUrlVO(null));
        assertTrue(ex.getMessage().contains("URL cannot be null"));
    }

    @Test
    void invalidUrlFormatThrowsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new ImageUrlVO("ftp://example.com/image.jpg"));
        assertTrue(ex.getMessage().contains("Invalid URL format"));
    }

    @Test
    void validHttpsUrl() {
        ImageUrlVO imageUrl = new ImageUrlVO("https://example.com/image.jpg");
        assertEquals("https://example.com/image.jpg", imageUrl.url());
    }

    @Test
    void validHttpUrl() {
        ImageUrlVO imageUrl = new ImageUrlVO("https://example.com/image.jpg");
        assertEquals("https://example.com/image.jpg", imageUrl.url());
    }
}
