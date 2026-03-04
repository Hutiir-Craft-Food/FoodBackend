package com.khutircraftubackend.product.image;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifDirectoryBase;
import com.drew.metadata.exif.ExifIFD0Directory;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;

@Component
public class ImageProcessing {

    private static final int ORIGIN_W = 1080;
    private static final int ORIGIN_H = 1920;
    private static final double SMALL_RATIO = 324.0 / 257.0;

    public Map<ImageSize, byte[]> process(InputStream inputStream) throws IOException {

        byte[] bytes = inputStream.readAllBytes();

        BufferedImage origin = readWithOrientation(bytes);
        origin = normalizeToOrigin(origin);

        BufferedImage square = cropToSquare(origin);
        BufferedImage smallBase = cropToSmallRatio(origin);

        return new EnumMap<>(Map.of(
                ImageSize.LARGE, toJpegBytes(origin),
                ImageSize.MEDIUM, toJpegBytes(resize(square, 438, 438)),
                ImageSize.SMALL, toJpegBytes(resize(smallBase, 324, 257)),
                ImageSize.THUMBNAIL, toJpegBytes(resize(square, 64, 64))
        ));
    }

    private BufferedImage normalizeToOrigin(BufferedImage raw) throws IOException {

        boolean isPortrait = raw.getHeight() > raw.getWidth();

        return Thumbnails.of(raw)
                .size(isPortrait ? ORIGIN_W : ORIGIN_H,
                        isPortrait ? ORIGIN_H : ORIGIN_W)
                .keepAspectRatio(true)
                .asBufferedImage();
    }

    private BufferedImage cropToSquare(BufferedImage source) throws IOException {

        int side = Math.min(source.getWidth(), source.getHeight());

        return Thumbnails.of(source)
                .sourceRegion(Positions.CENTER, side, side)
                .scale(1.0)
                .asBufferedImage();
    }

    private BufferedImage cropToSmallRatio(BufferedImage src) throws IOException {

        int w = src.getWidth();
        int h = src.getHeight();
        int cropW;
        int cropH;

        if ((double) w / h > SMALL_RATIO) {
            // зображення "ширше" за цільове співвідношення — обрізаємо по ширині
            cropH = h;
            cropW = (int) Math.round(h * SMALL_RATIO);
        } else {
            // зображення "вужче" — обрізаємо по висоті
            cropW = w;
            cropH = (int) Math.round(w * SMALL_RATIO);
        }

        return Thumbnails.of(src)
                .sourceRegion(Positions.CENTER, cropW, cropH)
                .scale(1.0)
                .asBufferedImage();
    }

    private BufferedImage resize(BufferedImage src, int w, int h) throws IOException {

        return Thumbnails.of(src)
                .size(w, h)
                .asBufferedImage();
    }

    private BufferedImage readWithOrientation(byte[] bytes) throws IOException {

        int rotation = readRotation(bytes);

        return Thumbnails.of(new ByteArrayInputStream(bytes))
                .rotate(rotation)
                .scale(1.0)
                .asBufferedImage();
    }

    private int readRotation(byte[] bytes) {

        try {
            Metadata metadata = ImageMetadataReader.readMetadata(new ByteArrayInputStream(bytes));

            ExifIFD0Directory directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);

            if (directory == null || !directory.containsTag(ExifDirectoryBase.TAG_ORIENTATION)) {
                return 0;
            }

            int orientation = directory.getInt(ExifDirectoryBase.TAG_ORIENTATION);

            return switch (orientation) {
                case 3 -> 180;
                case 6 -> 90;
                case 8 -> 270;
                default -> 0;
            };

        } catch (Exception e) {

            return 0;
        }
    }

    private BufferedImage removeAlphaChannel(BufferedImage image) {

        if (!image.getColorModel().hasAlpha()) {

            return image;
        }

        BufferedImage newImage = new BufferedImage(
                image.getWidth(),
                image.getHeight(),
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D g = newImage.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        g.drawImage(image, 0, 0, null);
        g.dispose();

        return newImage;
    }

    private byte[] toJpegBytes(BufferedImage image) throws IOException {

        BufferedImage rgb = removeAlphaChannel(image);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            boolean written = ImageIO.write(rgb, "jpg", baos);

            if (!written) {
                throw new IllegalArgumentException("No JPEG writer available");
            }

            return baos.toByteArray();
        }
    }

}
