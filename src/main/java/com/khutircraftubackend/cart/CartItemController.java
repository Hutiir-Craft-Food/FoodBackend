package com.khutircraftubackend.cart;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collection;

@RestController
@RequestMapping("/v1/cart")
@RequiredArgsConstructor
public class CartItemController {

	private final CartItemService cartItemService;

	@GetMapping("/items")
	public Collection<CartItemResponse> getCartItems(Principal principal) {

		return cartItemService.getCartItems(principal);
	}

	@GetMapping("/items/{productPriceId}")
	public CartItemResponse getCartItemsByPriceId(Principal principal,
												  @PathVariable Long productPriceId) {

		return cartItemService.getCartItemByPriceId(principal, productPriceId);
	}

	@PutMapping("/items/{productPriceId}")
	public CartItemResponse createOrUpdateCartItem(Principal principal,
												   @PathVariable Long productPriceId,
												   @Valid @RequestBody CartItemRequest request) {

		return cartItemService.createOrUpdateCartItem(
				principal,
				productPriceId,
				request
		);
	}

}
