package com.khutircraftubackend.product.image;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ImageProcessingTest {
    
    private ImageProcessing imageProcessing;
    
    @BeforeEach
    void setUp() {
        imageProcessing = new ImageProcessing();
    }
    
    @DisplayName("Helper method to create a test image and process it")
    private Map<ImageSize, byte[]> processImage(int width, int height, String format) throws IOException {
        
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, format, baos);
        
        InputStream input = new ByteArrayInputStream(baos.toByteArray());
        
        return imageProcessing.process(input);
    }
    
    @Test
    @DisplayName("Should return all image variants with non-empty byte arrays")
    void shouldReturnAllImageVariants() throws IOException {
        
        // when
        Map<ImageSize, byte[]> result = processImage(2000, 3000, "jpg");
        
        // then
        assertEquals(ImageSize.values().length, result.size());
        assertTrue(result.containsKey(ImageSize.LARGE));
        assertTrue(result.containsKey(ImageSize.MEDIUM));
        assertTrue(result.containsKey(ImageSize.SMALL));
        assertTrue(result.containsKey(ImageSize.THUMBNAIL));
        
        result.values().forEach(bytes -> {
            assertNotNull(bytes);
            assertTrue(bytes.length > 0);
        });
    }
    
    @ParameterizedTest
    @CsvSource({
            "2000, 3000, MEDIUM, 438, 438",
            "3000, 2000, SMALL, 324, 257",
            "3000, 1000, MEDIUM, 438, 438",
            "1000, 3000, MEDIUM, 438, 438",
            "200, 200, MEDIUM, 438, 438",
            "2000, 3000, THUMBNAIL, 64, 64"
    })
    @DisplayName("Should resize and crop images to expected dimensions")
    void shouldResizeToExpectedDimensions(int width, int height, ImageSize size, int expectedWidth, int expectedHeight) throws IOException {
        
        // given
        Map<ImageSize, byte[]> result = processImage(width, height, "jpg");
        
        // then
        BufferedImage processed = ImageIO.read(new ByteArrayInputStream(
                result.get(size)
        ));
        
        assertNotNull(processed);
        assertEquals(expectedWidth, processed.getWidth());
        assertEquals(expectedHeight, processed.getHeight());
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"jpg", "jpeg", "png"})
    @DisplayName("Should accept supported image formats")
    void shouldProcessSupportedFormats(String format) throws IOException {
        
        // given
        Map<ImageSize, byte[]> result = processImage(1000, 1000, format);
        
        // then
        assertNotNull(result);
    }
    
}