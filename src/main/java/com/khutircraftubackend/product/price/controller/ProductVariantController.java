package com.khutircraftubackend.product.price.controller;

import com.khutircraftubackend.product.price.response.ProductVariantResponse;
import com.khutircraftubackend.product.price.service.ProductVariantService;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/products")
@RequiredArgsConstructor
@Validated
public class ProductVariantController {

	public static final int MAX_PRICE_IDS = 100;

	private final ProductVariantService productVariantService;


	@GetMapping("/variants")
	@ResponseStatus(HttpStatus.OK)
	public List<ProductVariantResponse> getVariants(
			@RequestParam
			@NotEmpty(message = "priceIds не повинні бути порожніми")
			@Size(max = MAX_PRICE_IDS,
					message = "priceIds не повинні містити більше ніж "
							+ MAX_PRICE_IDS + " ідентифікаторів")
			List<@NotNull @Positive Long> priceIds) {

		return productVariantService.getVariants(priceIds);
	}

}
