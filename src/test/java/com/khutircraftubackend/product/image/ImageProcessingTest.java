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
            "3000, 1000, MEDIUM, 438, 438",
            "1000, 3000, MEDIUM, 438, 438",
            "200, 200, MEDIUM, 438, 438",
            "2000, 3000, SMALL, 257, 324",
            "3000, 2000, SMALL, 324, 257",
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
    
    @DisplayName("Should have large image with correct dimensions")
    @Test
    void largeShouldHaveExactDimensions() throws IOException {
        
        // given
        BufferedImage image = new BufferedImage(4000, 3000, BufferedImage.TYPE_INT_RGB);
        
        // when
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        
        // then
        Map<ImageSize, byte[]> result =
                imageProcessing.process(new ByteArrayInputStream(baos.toByteArray()));
        
        // and
        BufferedImage large = ImageIO.read(
                new ByteArrayInputStream(result.get(ImageSize.LARGE))
        );
        
        // assert
        assertEquals(1920, large.getWidth());
        assertEquals(1080, large.getHeight());
    }
    
    @DisplayName("Should crop to exact ratio")
    @Test
    void shouldCropToExactRatio() throws IOException {
        
        // given
        Map<ImageSize, byte[]> result = processImage(4000, 3000, "jpg");
        
        // then
        BufferedImage large = ImageIO.read(new ByteArrayInputStream(
                result.get(ImageSize.LARGE)
        ));
        
        double ratio = (double) large.getWidth() / large.getHeight();
        
        boolean isLandscape = large.getWidth() > large.getHeight();
        
        // assert
        if (isLandscape) {
            assertTrue(Math.abs(ratio - (16.0 / 9.0)) < 0.001);
        } else {
            assertTrue(Math.abs(ratio - (9.0 / 16.0)) < 0.001);
        }
    }
    
    @DisplayName("Should create small portrait image with correct dimensions and orientation")
    @Test
    void shouldCreateSmallPortraitCorrectly() throws IOException {
        
        // given
        Map<ImageSize, byte[]> result = processImage(3000, 4000, "jpg");
        
        // then
        BufferedImage small = ImageIO.read(new ByteArrayInputStream(
                result.get(ImageSize.SMALL)
        ));
        
        // assert
        assertEquals(257, small.getWidth());
        assertEquals(324, small.getHeight());
    }
    
    @DisplayName("Should create small landscape image with correct dimensions and orientation")
    @Test
    void shouldCreateSmallLandscapeCorrectly() throws IOException {
        
        // given
        Map<ImageSize, byte[]> result = processImage(4000, 3000, "jpg");
        
        // then
        BufferedImage small = ImageIO.read(new ByteArrayInputStream(
                result.get(ImageSize.SMALL)
        ));
        
        // assert
        assertEquals(324, small.getWidth());
        assertEquals(257, small.getHeight());
    }
    
    @DisplayName("Should handle very wide image and produce correct small variant")
    @Test
    void shouldHandleVeryWideImage() throws IOException {
        
        // given
        Map<ImageSize, byte[]> result = processImage(4000, 500, "jpg");
        
        // then
        BufferedImage small = ImageIO.read(new ByteArrayInputStream(
                result.get(ImageSize.SMALL)
        ));
        
        // assert
        assertEquals(324, small.getWidth());
        assertEquals(257, small.getHeight());
    }
    
    @DisplayName("Should handle very tall image and produce correct small variant")
    @Test
    void shouldHandleVeryTallImage() throws IOException {
        
        // given
        Map<ImageSize, byte[]> result = processImage(500, 4000, "jpg");
        
        // then
        BufferedImage small = ImageIO.read(new ByteArrayInputStream(
                result.get(ImageSize.SMALL)
        ));
        
        // assert
        assertEquals(257, small.getWidth());
        assertEquals(324, small.getHeight());
    }
    
    @DisplayName("Should handle small image that is smaller than target sizes and not upscale")
    @Test
    void shouldHandleSquareImage() throws IOException {
        
        // given
        Map<ImageSize, byte[]> result = processImage(100, 100, "jpg");
        
        // then
        BufferedImage small = ImageIO.read(new ByteArrayInputStream(
                result.get(ImageSize.SMALL)
        ));
        
        // assert
        assertEquals(324, small.getWidth());
        assertEquals(257, small.getHeight());
    }
    
}