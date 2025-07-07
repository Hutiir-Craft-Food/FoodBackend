package com.khutircraftubackend.product.image;

import com.khutircraftubackend.product.image.request.ProductImagesUploadAndChanges;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductImagesController {

    @Operation(
            summary = "Get all images for a product",
            description = "Retrieves a list of all images associated with the specified product ID",
            tags = {"Product"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved product images"),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ErrorResponseGeneric")))
    })
    @SecurityRequirement(name = "BearerAuth")
    ProductImagesResponse getProductImages(
            @Parameter(description = "ID of the product to fetch images for") Long productId);

    @Operation(
            summary = "Upload images for a product",
            description = """
                    Uploads and associates multiple images with a specific product.
                    Supported image formats: JPEG, PNG, GIF.
                    Maximum file size: 5MB per image.
                    First image will be marked as primary.
                    """,
            tags = {"Product"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Upload images for product"),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ErrorResponseGeneric"))),
            @ApiResponse(responseCode = "415", description = "Unsupported media type",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ErrorResponseGeneric")))
    })
    @SecurityRequirement(name = "BearerAuth")
    ProductImagesResponse uploadProductImages
            (@Parameter(description = "ID of the product to associate images with") Long productId,
             @RequestBody(description = "") ProductImagesUploadAndChanges json,
             @RequestBody(description = "List of image files") List<MultipartFile> files);

    @Operation(
            summary = "Replace images for product",
            description = """
                    We update several images with a specific product.
                    Supported image formats: JPEG, PNG, GIF.
                    Maximum file size: 5 MB per image.
                    """,
            tags = {"Product"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Replace images for product"),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ErrorResponseGeneric"))),
            @ApiResponse(responseCode = "415", description = "Unsupported media type",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ErrorResponseGeneric")))
    })
    @SecurityRequirement(name = "BearerAuth")
    ProductImagesResponse changesProductImages //TODO откореткировать документ
            (@Parameter(description = "The ID of the product in which the images need to be changed") Long productId,
             @RequestBody(description = "Number images") ProductImagesUploadAndChanges request);

    @Operation(
            summary = "Delete product images by positions",
            description = """
                    Deletes product images either completely (if no positions specified) 
                    or partially by specified position IDs.
                    Returns 204 (No Content) on successful deletion.
                    """,
            tags = {"Product"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully retrieved product images"),
            @ApiResponse(responseCode = "400", description = "Invalid position IDs format",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ErrorResponseGeneric"))),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ErrorResponseGeneric")))
    })
    @SecurityRequirement(name = "BearerAuth")
    void deleteProductImagesByPositions
            (@Parameter(
                    description = "ID of the product to delete images from") Long productId,
             @Parameter(
                     description = """
                             List of image positions to delete. 
                             If empty or not provided - deletes all product images.
                             Example: ?position=1&position=2
                             """)
             @RequestParam(value = "position", required = false) List<Integer> positionIds);

}
