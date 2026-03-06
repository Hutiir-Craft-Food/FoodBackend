package com.khutircraftubackend.product.image;

import com.khutircraftubackend.product.ProductService;
import com.khutircraftubackend.product.image.request.ProductImageUploadRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/v1/products/{productId}/images-test")
@RequiredArgsConstructor
public class SCRUM210Controller {

    private final ProductImageService productImageService;
    private final ProductService productService;


    // --- SCRUM-210
    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Map<String, Object>> testSCRUM210 (
            @PathVariable Long productId,
            @Valid @RequestPart(value = "json") ProductImageUploadRequest json,
            @RequestPart(value = "files") List<MultipartFile> files
    ) {
        List<ProductImageEntity> productImageEntities = productImageService
                .createImagesInternally(productService.findProductById(productId), json, files);

        return productImageEntitiesToMap(productImageEntities);
    }


    private List<Map<String, Object>> productImageEntitiesToMap(List<ProductImageEntity> entities) {
        return entities.stream().map(image -> {
            Map<String, Object> imageNode = new LinkedHashMap<>();

            String productName = (image.getProduct() != null) ? image.getProduct().getName() : null;
            imageNode.put("productName", productName);

            imageNode.put("position", image.getPosition());

            List<Map<String, Object>> variantsList = new ArrayList<>();
            if (image.getVariants() != null) {
                for (ProductImageVariantEntity variant : image.getVariants()) {
                    Map<String, Object> variantNode = new LinkedHashMap<>();
                    variantNode.put("tsSize", variant.getTsSize() != null ? variant.getTsSize().name() : null);
                    variantNode.put("link", variant.getLink());
                    variantsList.add(variantNode);
                }
            }
            imageNode.put("variants", variantsList);

            return imageNode;
        }).collect(Collectors.toList());
    }

}
