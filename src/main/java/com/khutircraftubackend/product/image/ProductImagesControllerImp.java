package com.khutircraftubackend.product.image;

import com.khutircraftubackend.product.image.request.ProductImagesChangeRequest;
import com.khutircraftubackend.product.image.request.ProductImagesUploadRequest;
import com.khutircraftubackend.product.image.response.ProductImagesResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
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
    public ProductImagesResponse uploadProductImages(
            @PathVariable Long productId,
            @RequestPart(value = "files") List<MultipartFile> imageFiles,
            @Valid @RequestPart(value = "metadata") ProductImagesUploadRequest metadata
    ) {
        return service.createImages(productId, metadata, imageFiles);
    }

    @PutMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasRole('ADMIN') or (hasRole('SELLER') and @productService.canModifyProduct(#productId))")
    @ResponseStatus(HttpStatus.OK)
    public ProductImagesResponse changesProductImages(
            @PathVariable Long productId,
            @Valid @RequestBody ProductImagesChangeRequest request
    ) {
        return service.updateImages(productId, request);
    }

    @DeleteMapping()
    @PreAuthorize("hasRole('ADMIN') or (hasRole('SELLER') and @productService.canModifyProduct(#productId))")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProductImagesByPositions(
            @PathVariable Long productId,
            @RequestParam(value = "position", required = false) List<Integer> positionIds
    ) {
        service.deleteProductImages(productId, positionIds);
    }
}

