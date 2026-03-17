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
    private static final double SMALL_RATIO = 324.0 / 257.0;
    
    public Map<ImageSize, byte[]> process(InputStream inputStream) {
        
        byte[] bytes;
        try (inputStream) {
            bytes = inputStream.readAllBytes();
        } catch (IOException e) {
            throw new ImageProcessingException(ERROR_READ_INPUT_STREAM, e);
        }
        
        BufferedImage origin = readWithOrientation(bytes);
        origin = createOrigin(origin);
        
        BufferedImage square = createSquareCrop(origin);
        BufferedImage smallBase = createSmallRatioCrop(origin);
        
        BufferedImage medium = resize(square, ImageSize.MEDIUM.width, ImageSize.MEDIUM.height);
        BufferedImage thumbnail = resize(medium, ImageSize.THUMBNAIL.width, ImageSize.THUMBNAIL.height);
        BufferedImage small = resize(smallBase, ImageSize.SMALL.width, ImageSize.SMALL.height);
        
        Map<ImageSize, byte[]> result = new EnumMap<>(ImageSize.class);
        
        result.put(ImageSize.LARGE, toJpegBytes(origin));
        result.put(ImageSize.MEDIUM, toJpegBytes(medium));
        result.put(ImageSize.SMALL, toJpegBytes(small));
        result.put(ImageSize.THUMBNAIL, toJpegBytes(thumbnail));
        
        return result;
    }
    
    private BufferedImage createOrigin(BufferedImage raw) {
        
        boolean isPortrait = raw.getHeight() > raw.getWidth();
        int targetWidth = isPortrait ? ORIGIN_W : ORIGIN_H;
        int targetHeight = isPortrait ? ORIGIN_H : ORIGIN_W;
        
        try {
            return Thumbnails.of(raw)
                    .size(targetWidth, targetHeight)
                    .keepAspectRatio(true)
                    .asBufferedImage();
        } catch (IOException e) {
            throw new ImageProcessingException(ERROR_CREATE_ORIGIN_IMAGE, e);
        }
    }
    
    private BufferedImage createSquareCrop(BufferedImage source) {
        
        int side = Math.min(source.getWidth(), source.getHeight());
        
        try {
            return Thumbnails.of(source)
                    .sourceRegion(Positions.CENTER, side, side)
                    .scale(1.0)
                    .asBufferedImage();
        } catch (IOException e) {
            throw new ImageProcessingException(ERROR_CREATE_SQUARE_IMAGE, e);
        }
    }
    
    private BufferedImage createSmallRatioCrop(BufferedImage src) {
        
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
        try {
            return Thumbnails.of(src)
                    .sourceRegion(Positions.CENTER, cropW, cropH)
                    .scale(1.0)
                    .asBufferedImage();
        } catch (IOException e) {
            throw new ImageProcessingException(ERROR_IMAGE_TOO_SMALL, e);
        }
        
    }
    
    private BufferedImage resize(BufferedImage src, int w, int h) {
        
        try {
            return Thumbnails.of(src)
                    .size(w, h)
                    .asBufferedImage();
        } catch (IOException e) {
            throw new ImageProcessingException(ERROR_RESIZE_IMAGE, e);
        }
    }
    
    private BufferedImage readWithOrientation(byte[] bytes) {
        
        BufferedImage image;
        try {
            image = ImageIO.read(new ByteArrayInputStream(bytes));
        } catch (IOException e) {
            throw new ImageProcessingException(ERROR_READ_INPUT_STREAM, e);
        }
        
        int rotation = readRotation(bytes);
        
        try {
            if(image == null) {
                throw new ImageProcessingException(ERROR_READ_INPUT_STREAM);
            }
            
            return Thumbnails.of(image)
                    .rotate(rotation)
                    .scale(1.0)
                    .asBufferedImage();
        } catch (IOException e) {
            throw new ImageProcessingException(ERROR_READ_ORIENT_IMAGE, e);
        }
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
            
        } catch (IOException | MetadataException e) {
            throw new ImageProcessingException(ERROR_METADATA_READ, e);
        } catch (com.drew.imaging.ImageProcessingException e) {
            throw new ImageProcessingException(ERROR_PROCESSING_METADATA, e);
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
    
    private byte[] toJpegBytes(BufferedImage image) {
        
        BufferedImage rgb = removeAlphaChannel(image);
        
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Thumbnails.of(rgb)
                    .scale(1.0)
                    .outputFormat("jpg")
                    .toOutputStream(baos);
            
            return baos.toByteArray();
        } catch (IOException e) {
            throw new ImageProcessingException(ERROR_TO_JPEG_CONVERSION, e);
        }
    }
    
}
