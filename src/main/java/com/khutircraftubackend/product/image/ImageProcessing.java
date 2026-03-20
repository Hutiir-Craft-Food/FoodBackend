package com.khutircraftubackend.product.image;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifDirectoryBase;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.khutircraftubackend.product.image.exception.ImageProcessingException;
import lombok.extern.slf4j.Slf4j;
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

import static com.khutircraftubackend.product.image.response.ProductImageResponseMessages.*;

@Component
@Slf4j
public class ImageProcessing {
    
    private static final int ORIGIN_W = 1080;
    private static final int ORIGIN_H = 1920;
    
    public Map<ImageSize, byte[]> process(InputStream inputStream) {
        
        byte[] bytes;
        try {
            bytes = inputStream.readAllBytes();
            
            BufferedImage origin = readWithOrientation(bytes);
            origin = createOrigin(origin);
            
            BufferedImage square = createSquare(origin);
            BufferedImage smallBase = createSmall(origin);
            
            BufferedImage medium = resize(square, ImageSize.MEDIUM.width, ImageSize.MEDIUM.height);
            BufferedImage thumbnail = resize(medium, ImageSize.THUMBNAIL.width, ImageSize.THUMBNAIL.height);
            
            Map<ImageSize, byte[]> result = new EnumMap<>(ImageSize.class);
            
            result.put(ImageSize.LARGE, toJpegBytes(origin));
            result.put(ImageSize.MEDIUM, toJpegBytes(medium));
            result.put(ImageSize.SMALL, toJpegBytes(smallBase));
            result.put(ImageSize.THUMBNAIL, toJpegBytes(thumbnail));
            
            return result;
            
        } catch (IOException e) {
            log.error("Failed to process image", e);
            throw new ImageProcessingException(ERROR_IMAGE_PROCESSING, e);
        } catch (RuntimeException e) {
            log.error("Unexpected error during image processing", e);
            throw new ImageProcessingException(UNEXPECTED_IMAGE_PROCESSING, e);
        }
    }
    
    private BufferedImage createOrigin(BufferedImage raw) throws IOException {
        
        boolean isPortrait = raw.getHeight() > raw.getWidth();
        
        int targetWidth = isPortrait ? ORIGIN_W : ORIGIN_H;
        int targetHeight = isPortrait ? ORIGIN_H : ORIGIN_W;
        
        double targetRatio = (double) targetWidth / targetHeight;
        
        BufferedImage cropped = cropToRatio(raw, targetRatio);
        
        return Thumbnails.of(cropped)
                .size(targetWidth, targetHeight)
                .asBufferedImage();
    }
    
    private BufferedImage createSquare(BufferedImage source) throws IOException {
        
        int side = Math.min(source.getWidth(), source.getHeight());
        
        return Thumbnails.of(source)
                .sourceRegion(Positions.CENTER, side, side)
                .scale(1.0)
                .asBufferedImage();
    }
    
    private BufferedImage createSmall(BufferedImage src) throws IOException {
        
        boolean isPortrait = src.getHeight() > src.getWidth();
        
        int targetWidth = isPortrait ? ImageSize.SMALL.width : ImageSize.SMALL.height;
        int targetHeight = isPortrait ? ImageSize.SMALL.height : ImageSize.SMALL.width;
        
        double targetRatio = (double) targetWidth / targetHeight;
        
        BufferedImage small = cropToRatio(src, targetRatio);
        
        return Thumbnails.of(small)
                .size(targetWidth, targetHeight)
                .asBufferedImage();
    }
    
    private BufferedImage cropToRatio(BufferedImage src, double ratio) throws IOException {
        
        int w = src.getWidth();
        int h = src.getHeight();
        
        int cropW;
        int cropH;
        
        if ((double) w / h > ratio) {
            cropH = h;
            cropW = (int) Math.round(h * ratio);
        } else {
            cropW = w;
            cropH = (int) Math.round(w / ratio);
        }
        
        return Thumbnails.of(src)
                .sourceRegion(Positions.CENTER, cropW, cropH)
                .scale(1.0)
                .asBufferedImage();
    }
    
    private BufferedImage resize(BufferedImage src, int w, int h) throws IOException {
        
        return Thumbnails.of(src)
                .size(w, h)
                .keepAspectRatio(true)
                .asBufferedImage();
    }
    
    private BufferedImage readWithOrientation(byte[] bytes) throws IOException {
        
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
        
        if (image == null) {
            throw new ImageProcessingException(ERROR_IMAGE_READ);
        }
        
        int rotation = readRotation(bytes);
        
        return Thumbnails.of(image)
                .rotate(rotation)
                .scale(1.0)
                .asBufferedImage();
    }
    
    private int readRotation(byte[] bytes) throws IOException {
        
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
        } catch (com.drew.imaging.ImageProcessingException | MetadataException e) {
            log.warn("Failed to read image metadata for orientation, defaulting to 0 rotation", e);
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
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Thumbnails.of(rgb)
                .scale(1.0)
                .outputFormat("jpg")
                .toOutputStream(baos);
        
        return baos.toByteArray();
    }
    
}
