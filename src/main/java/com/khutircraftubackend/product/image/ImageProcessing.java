package com.khutircraftubackend.product.image;

import com.khutircraftubackend.product.image.exception.ImageProcessingException;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;

@Component
public class ImageProcessing {

    private static final int MAX_WIDTH = 1920;
    private static final int MAX_HEIGHT = 1080;
    private static final Tika TIKA = new Tika();

    public Map<ImageSize, byte[]> process(byte[] bytes) throws ImageProcessingException {

        BufferedImage normalizedImage = normalize(bytes);

        BufferedImage mediumImage = cropToSize(normalizedImage, 438, 438);
        BufferedImage smallImage = cropToSize(mediumImage, 324, 257);
        BufferedImage thumbnailImage = cropToSize(mediumImage, 64, 64);

        return new EnumMap<>(Map.of(
                ImageSize.LARGE, toByteArray(normalizedImage),
                ImageSize.MEDIUM, toByteArray(mediumImage),
                ImageSize.SMALL, toByteArray(smallImage),
                ImageSize.THUMBNAIL, toByteArray(thumbnailImage)
        ));
    }

    private BufferedImage normalize(byte[] rawImage) throws ImageProcessingException {

        try {
            InputStream is = new ByteArrayInputStream(rawImage);
            BufferedImage originalImage = ImageIO.read(is);

            // TODO:
            //  String detectedMimeType = TIKA.detect(is);
            //  if (detectedMimeType == "image/png" && originalImage.getColorModel().hasAlpha()):
            //      then convert to jpeg (because of the alpha channel).
            //  do it before resizing, to avoid quality loss

            int width = originalImage.getWidth();
            int height = originalImage.getHeight();

            if (Math.min(width, height) < 438) {
                throw new ImageProcessingException("Image is too small for processing");
            }

            if (height <= MAX_HEIGHT && width <= MAX_WIDTH) {
                return originalImage;
            }

            return resize(originalImage, MAX_WIDTH, MAX_HEIGHT);
        } catch (IOException ex) {
            throw new ImageProcessingException("Failed to normalize image", ex);
        }
    }

    private BufferedImage cropToSize(BufferedImage sourceImage, int width, int height) throws ImageProcessingException {
        if (width > sourceImage.getWidth() || height > sourceImage.getHeight()) {
            throw new ImageProcessingException("Crop dimensions exceed source image dimensions");
        }

        int minSourceDimension = Math.min(sourceImage.getWidth(), sourceImage.getHeight());
        int maxTargetDimension = Math.max(width, height);

        // TODO: make sure it works the same way for both portrait and landscape images
        double scale = (double) maxTargetDimension / minSourceDimension;

        try {
            BufferedImage resizedImage = resize(sourceImage, scale);
            if (width == resizedImage.getWidth() && height == resizedImage.getHeight()) {
                return resizedImage;
            }

            return Thumbnails.of(resizedImage)
                    .sourceRegion(Positions.CENTER, width, height)
                    .size(width, height)
                    .asBufferedImage();
        } catch (IOException e) {
            throw new ImageProcessingException("Failed to crop image", e);
        }
    }

    private BufferedImage resize(BufferedImage sourceImage, double scale) throws ImageProcessingException {
        try {
            return Thumbnails.of(sourceImage)
                    .scale(scale)
                    .asBufferedImage();
        } catch (IOException e) {
            throw new ImageProcessingException("Failed to resize image", e);
        }
    }

    private BufferedImage resize(BufferedImage sourceImage, int width, int height) throws ImageProcessingException {
        try {
            return Thumbnails.of(sourceImage)
                .size(width, height)
                .keepAspectRatio(true)
                .asBufferedImage();
        } catch (IOException ex) {
            throw new ImageProcessingException("Failed to resize image", ex);
        }
    }

    public byte[] toByteArray(BufferedImage image) throws ImageProcessingException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Thumbnails.of(image)
                    .scale(1.0)
                    .outputFormat("jpg")
                    .toOutputStream(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new ImageProcessingException("Failed to convert image to byte array", e);
        }
    }

}
