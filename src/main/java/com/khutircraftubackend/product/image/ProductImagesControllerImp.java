package com.khutircraftubackend.product.image;

import com.khutircraftubackend.product.image.request.ProductImagesUploadAndChanges;
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
public class ProductImagesControllerImp implements ProductImagesController {

    private final ProductImagesService service;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ProductImagesResponse getProductImages(@PathVariable Long productId) {
        return service.getProductImages(productId);
    }

    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasRole('ADMIN') or (hasRole('SELLER') and @productService.canModifyProduct(#productId))")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductImagesResponse uploadProductImages (
            @PathVariable Long productId,
            @Valid @RequestPart(value = "json") ProductImagesUploadAndChanges json,
            @RequestPart(value = "files") List<MultipartFile> files
    ){
        return service.uploadImages(productId, json, files);
    }

    @PutMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasRole('ADMIN') or (hasRole('SELLER') and @productService.canModifyProduct(#productId))")
    @ResponseStatus(HttpStatus.OK)
    public ProductImagesResponse changesProductImages(@PathVariable Long productId,
                                                      @RequestBody ProductImagesUploadAndChanges request) {
        return service.changesPosition(productId, request);
    }

    @DeleteMapping()
    @PreAuthorize("hasRole('ADMIN') or (hasRole('SELLER') and @productService.canModifyProduct(#productId))")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProductImagesByPositions(@PathVariable Long productId,
                @RequestParam(value = "position", required = false) List<Integer> positionIds) {
        service.deleteProductImages(productId, positionIds);
    }
}

