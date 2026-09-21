package com.khutircraftubackend.cart;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
