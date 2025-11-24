//package com.khutircraftubackend.product.image;
//
//import com.khutircraftubackend.product.image.request.ProductImageUploadRequest;
//import com.khutircraftubackend.product.image.request.ProductImageChangeRequest;
//import com.khutircraftubackend.product.image.response.ProductImageResponse;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.Parameter;
//import io.swagger.v3.oas.annotations.media.Content;
//import io.swagger.v3.oas.annotations.media.Schema;
//import io.swagger.v3.oas.annotations.parameters.RequestBody;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.responses.ApiResponses;
//
//import io.swagger.v3.oas.annotations.security.SecurityRequirement;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RequestPart;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.List;
//
//public interface ProductImageController {
//
//    @Operation(
//            summary = "Get all images for a product",
//            description = "Retrieves a list of all images associated with the specified product ID",
//            tags = {"Product"})
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Successfully retrieved product images"),
//            @ApiResponse(responseCode = "404", description = "Product not found",
//                    content = @Content(schema = @Schema(ref = "#/components/schemas/ErrorResponseGeneric")))
//    })
//    @SecurityRequirement(name = "BearerAuth")
//    ProductImageResponse getProductImages(
//            @Parameter(description = "ID of the product to fetch images for") Long productId);
//
//    @Operation(
//            summary = "Upload images for a product",
//            description = """
//                    Uploads and associates multiple images with a specific product.
//                    Supported image formats: JPEG, PNG, GIF.
//
//                      **Limitations:**
//                      - Maximum product images: 5
//                      - Supported formats: JPEG, PNG, GIF
//                      - Max file size: 5 MB per image
//
//                       **Position System:**
//                       - 0 - Primary image (displayed first)
//                       - 1-4 - Secondary images
//                    """,
//            tags = {"Product"})
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "201", description = "Upload images for product"),
//            @ApiResponse(responseCode = "400", description = "Bad request",
//                    content = @Content(schema = @Schema(ref = "#/components/schemas/ErrorResponseGeneric"))),
//            @ApiResponse(responseCode = "404", description = "Product not found",
//                    content = @Content(schema = @Schema(ref = "#/components/schemas/ErrorResponseGeneric"))),
//            @ApiResponse(responseCode = "415", description = "Unsupported media type",
//                    content = @Content(schema = @Schema(ref = "#/components/schemas/ErrorResponseGeneric")))
//    })
//    @SecurityRequirement(name = "BearerAuth")
//    ProductImageResponse uploadProductImages
//            (@Parameter(description = "ID of the product to associate images with", example = "6") Long productId,
//             @Parameter(
//                     description = "JSON metadata for image processing",
//                     required = true,
//                     schema = @Schema(implementation = ProductImageUploadRequest.class)
//             )
//             @RequestPart("metadata") ProductImageUploadRequest json,
//             @Parameter(
//                     description = "List of image files (PNG, JPEG, etc.)")
//             @RequestPart("files") List<MultipartFile> files);
//
//    @Operation(
//            summary = "Replace images for product",
//            description = """
//                    Allows reordering existing product images by changing their display positions.
//
//                       **Key Features:**
//                       - Images must already belong to the product
//                       - Supports updating positions for multiple images in a single request
//                       - Positions must be unique within the request (0-4)
//                       - Only changes the display order, keeps original images
//
//                       **Position System:**
//                       - 0 - Primary image (displayed first)
//                       - 1-4 - Secondary images
//                    """,
//            tags = {"Product"})
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Replace images for product"),
//            @ApiResponse(responseCode = "400", description = "Bad request",
//                    content = @Content(schema = @Schema(ref = "#/components/schemas/ErrorResponseGeneric"))),
//            @ApiResponse(responseCode = "404", description = "Product not found",
//                    content = @Content(schema = @Schema(ref = "#/components/schemas/ErrorResponseGeneric"))),
//            @ApiResponse(responseCode = "415", description = "Unsupported media type",
//                    content = @Content(schema = @Schema(ref = "#/components/schemas/ErrorResponseGeneric")))
//    })
//    @SecurityRequirement(name = "BearerAuth")
//    ProductImageResponse changesProductImages
//            (@Parameter(description = "The ID of the product in which the images need to be changed") Long productId,
//             @RequestBody(description = "Number images") ProductImageChangeRequest request);
//
//    @Operation(
//            summary = "Delete product images by positions",
//            description = """
//                    Deletes product images either completely (if no positions specified)
//                    or partially by specified position IDs.
//                    Returns 204 (No Content) on successful deletion.
//                    """,
//            tags = {"Product"})
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "204", description = "Successfully retrieved product images"),
//            @ApiResponse(responseCode = "400", description = "Invalid position IDs format",
//                    content = @Content(schema = @Schema(ref = "#/components/schemas/ErrorResponseGeneric"))),
//            @ApiResponse(responseCode = "404", description = "Product not found",
//                    content = @Content(schema = @Schema(ref = "#/components/schemas/ErrorResponseGeneric")))
//    })
//    @SecurityRequirement(name = "BearerAuth")
//    void deleteProductImagesByPositions
//            (@Parameter(
//                    description = "ID of the product to delete images from") Long productId,
//             @Parameter(
//                     description = """
//                             List of image positions to delete.
//                             If empty or not provided - deletes all product images.
//                             Example: ?position=1&position=2
//                             """)
//             @RequestParam(value = "position", required = false) List<Integer> positionIds);
//}

package com.khutircraftubackend.product.image;

import com.khutircraftubackend.product.image.request.ProductImageUploadRequest;
import com.khutircraftubackend.product.image.request.ProductImageChangeRequest;
import com.khutircraftubackend.product.image.response.ProductImageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductImageController {

    // =========================================================================
    //  GET PRODUCT IMAGES
    // =========================================================================

    @Operation(
            summary = "Fetch product images with variants",
            description = """
                    Returns all image entries for selected product.  
                    Each image contains:
                    
                    - Unique image ID  
                    - Display position (0 = primary)  
                    - List of generated variants (THUMBNAIL, SMALL, MEDIUM, LARGE)  
                    - Direct links to stored image files  
                    
                    The response is guaranteed to include all variants for each image.
                    """,
            tags = {"Product Images"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Images successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ErrorResponseGeneric")))
    })
    @SecurityRequirement(name = "BearerAuth")
    ProductImageResponse getProductImages(
            @Parameter(description = "Product ID", example = "42")
            Long productId
    );

    // =========================================================================
    //  UPLOAD IMAGES
    // =========================================================================

    @Operation(
            summary = "Upload and register product images",
            description = """
                    Uploads one or more images and registers them for a product.
                    
                    **Rules & Processing Logic:**
                    - Maximum images per product: **5**
                    - Format support: **JPEG, PNG, GIF**
                    - Maximum file size per image: **5MB**
                    - Backend automatically generates 4 image variants:  
                      `THUMBNAIL`, `SMALL`, `MEDIUM`, `LARGE`
                    - Variants are stored in separate rows in `product_image_variants`
                      linked to a single `product_images` entry.
                    
                    **Position System**
                    - 0 → Primary image  
                    - 1–4 → Secondary images  
                    - Positions must be unique  
                    
                    If positions are not provided, they are auto-assigned sequentially.
                    """,
            tags = {"Product Images"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Images uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ErrorResponseGeneric"))),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "415", description = "Unsupported format")
    })
    @SecurityRequirement(name = "BearerAuth")
    ProductImageResponse uploadProductImages(
            @Parameter(description = "Target product ID", required = true)
            Long productId,

            @Parameter(description = "JSON metadata for upload request", required = true,
                    schema = @Schema(implementation = ProductImageUploadRequest.class))
            @RequestPart("metadata") ProductImageUploadRequest metadata,

            @Parameter(description = "Uploaded image files")
            @RequestPart("files") List<MultipartFile> files
    );

    // =========================================================================
    //  REORDER IMAGES
    // =========================================================================

    @Operation(
            summary = "Reorder product images",
            description = """
                    Updates the display order of existing product images.

                    **Key Properties:**
                    - Only existing images can be reordered  
                    - Positions must be unique inside request  
                    - Allowed position range: **0–4**
                    - All variants belonging to an image inherit the new position
                    
                    **Conflict Handling:**
                    - If two images target the same position, the system internally resolves the conflict
                      using temporary offsets.
                    """,
            tags = {"Product Images"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Images successfully reordered"),
            @ApiResponse(responseCode = "400", description = "Position conflict or invalid request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ErrorResponseGeneric"))),
            @ApiResponse(responseCode = "404", description = "Product or image not found")
    })
    @SecurityRequirement(name = "BearerAuth")
    ProductImageResponse changesProductImages(
            @Parameter(description = "Product ID") Long productId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "New image positions",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ProductImageChangeRequest.class)
                    )
            )
            ProductImageChangeRequest request
    );

    // =========================================================================
    //  DELETE IMAGES
    // =========================================================================

    @Operation(
            summary = "Delete product images (by position or all)",
            description = """
                    Deletes product images together with all their variants.

                    **Usage:**
                    - If list of positions is provided → deletes images at those positions  
                    - If empty → deletes *all* images for the product  
                    
                    Removes:
                    - `product_images` record  
                    - All related rows from `product_image_variants`  
                    - Physical files from storage
                    """,
            tags = {"Product Images"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Images deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid positions format",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ErrorResponseGeneric"))),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @SecurityRequirement(name = "BearerAuth")
    void deleteProductImagesByPositions(
            @Parameter(description = "Product ID") Long productId,

            @Parameter(description = """
                    List of image positions to delete.  
                    Example: ?position=0&position=2  
                    If omitted → deletes all images.
                    """)
            @RequestParam(value = "position", required = false)
            List<Integer> positionIds
    );
}
