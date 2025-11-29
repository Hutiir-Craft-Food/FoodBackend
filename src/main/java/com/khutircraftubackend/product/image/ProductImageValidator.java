package com.khutircraftubackend.product.image;

import com.khutircraftubackend.product.image.exception.*;
import com.khutircraftubackend.product.image.request.ProductImageChangeRequest;
import com.khutircraftubackend.product.image.request.ProductImageUploadRequest;
import com.khutircraftubackend.product.image.response.ProductImageResponseMessages;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ProductImageValidator {

    private static final Set<Integer> VALID_POSITIONS = Set.of(0, 1, 2, 3, 4);
    private static final Long MAX_UPLOAD_FILE_COUNT = 5L;

    public void validateDeletePositions(List<Integer> positions, List<ProductImageEntity> images) {

        if (images.isEmpty()) {
            throw new ImageNotFoundException(
                    String.format(ProductImageResponseMessages.ERROR_NOT_FOUND_POSITION, VALID_POSITIONS));
        }

        if (positions == null || positions.isEmpty()) {
            return;
        }

        validateRange(positions);
        validateExistence(positions, images);
    }

    private void validateRange(List<Integer> positions) {
        Set<Integer> invalid = positions.stream()
                .filter(p -> !VALID_POSITIONS.contains(p))
                .collect(Collectors.toSet());

        if (!invalid.isEmpty()) {
            throw new ImageValidationException(
                    String.format(ProductImageResponseMessages.ERROR_INVALID_POSITION, invalid));
        }
    }

    private void validateExistence(List<Integer> positions, List<ProductImageEntity> images){
        Set<Integer> existingPositions = images.stream()
                .map(ProductImageEntity::getPosition)
                .collect(Collectors.toSet());

        Set<Integer> notFound = positions.stream()
                .filter(p -> !existingPositions.contains(p))
                .collect(Collectors.toSet());

        if (!notFound.isEmpty()) {
            throw new ImageNotFoundException(
                    String.format(ProductImageResponseMessages.ERROR_NOT_FOUND_POSITION, notFound));
        }
    }

    public void validateImageIds(ProductImageChangeRequest request,
                                  List<ProductImageEntity> dbImages) {
        Set<Long> dbIds = dbImages.stream().map(ProductImageEntity::getId).collect(Collectors.toSet());
        Set<Long> missing = request.images().stream()
                .map(ProductImageChangeRequest.Image::id)
                .filter(id -> !dbIds.contains(id))
                .collect(Collectors.toSet());

        if (!missing.isEmpty()) {
            throw new ImageNotFoundException(
                    String.format(ProductImageResponseMessages.ERROR_IMAGE_NOT_FOUND_BY_ID, missing));
        }

        validateImagesCount(request, dbImages);
    }

    private void validateImagesCount(ProductImageChangeRequest request,
                      List<ProductImageEntity> allImages) {
        if (allImages.size() != request.images().size()) {
            throw new ImagesCountMismatchException(String.format(
                    ProductImageResponseMessages.ERROR_IMAGES_COUNT_MISMATCH,
                    request.images().size(),
                    allImages.size()));
        }
    }

    public void validateUploadRequest(List<ProductImageEntity> existingImages,
                                      ProductImageUploadRequest request, List<MultipartFile> files) {
        if (files.size() > MAX_UPLOAD_FILE_COUNT) {
            throw new TooManyImagesException(String.format(
                    ProductImageResponseMessages.ERROR_TOO_MANY_IMAGES, MAX_UPLOAD_FILE_COUNT));
        }

        if (files.size() != request.images().size()) {
            throw new ImagesCountMismatchException(String.format(
                    ProductImageResponseMessages.ERROR_IMAGES_COUNT_MISMATCH,
                    files.size(),
                    request.images().size()));
        }

        if (hasAnyDuplicatePosition(existingImages, request)) {
            throw new PositionAlreadyExistsException(ProductImageResponseMessages.ERROR_POSITION_ALREADY_EXISTS);
        }
    }

    private boolean hasAnyDuplicatePosition(List<ProductImageEntity> existingImages, ProductImageUploadRequest request) {
        Set<Integer> existingPositions = existingImages.stream()
                .map(ProductImageEntity::getPosition)
                .collect(Collectors.toSet());

        return request.images().stream()
                .map(ProductImageUploadRequest.ImageUpload::position)
                .anyMatch(existingPositions::contains);
    }
}