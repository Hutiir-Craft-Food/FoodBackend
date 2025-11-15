package com.khutircraftubackend.product.image;

import com.khutircraftubackend.product.image.request.ProductImageUploadRequest;
import com.khutircraftubackend.product.image.request.ProductImageChangeRequest;
import com.khutircraftubackend.product.image.response.ProductImageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/v1/products/{productId}/images")
@Slf4j
@RequiredArgsConstructor
public class ProductImageControllerImp implements ProductImageController {

    private final ProductImageService service;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ProductImageResponse getProductImages(@PathVariable Long productId) {
        return service.getProductImages(productId);
    }

    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasRole('ADMIN') or (hasRole('SELLER') and @productService.assertCanModifyProduct(#productId))")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductImageResponse uploadProductImages(
            @PathVariable Long productId,
            @Valid @RequestPart(value = "json") ProductImageUploadRequest json,
            @RequestPart(value = "files") List<MultipartFile> files
    ) {
        return service.createImages(productId, json, files);
    }

    @PutMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasRole('ADMIN') or (hasRole('SELLER') and @productService.assertCanModifyProduct(#productId))")
    @ResponseStatus(HttpStatus.OK)
    public ProductImageResponse changesProductImages(@PathVariable Long productId,
                                                     @RequestBody ProductImageChangeRequest request) {
        return service.updateImages(productId, request);
    }

    @DeleteMapping()
    @PreAuthorize("hasRole('ADMIN') or (hasRole('SELLER') and @productService.assertCanModifyProduct(#productId))")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProductImagesByPositions(@PathVariable Long productId,
                                               @RequestParam(value = "position", required = false) List<Integer> positionIds) {
        service.deleteProductImages(productId, positionIds);
    }
}