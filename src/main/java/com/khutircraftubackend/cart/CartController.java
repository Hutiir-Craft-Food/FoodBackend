package com.khutircraftubackend.cart;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartItemRepository cartItemRepository;
    private final CartItemMapper cartItemMapper;

    @GetMapping("/items")
    public Collection<CartItemResponse> getCartItems() {
        var myCartItems = cartItemRepository.findByUserId(3L);
        return cartItemMapper.toCartItemResponse(myCartItems);
    }

    @GetMapping("/items/{productPriceId}")
    public CartItemResponse getCartItemByPriceId(@PathVariable Long productPriceId) {
        var cartItem = cartItemRepository.findByUserIdAndProductPriceId(3L, productPriceId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        return cartItemMapper.toCartItemResponse(cartItem);
    }
}
