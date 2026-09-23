package com.khutircraftubackend.cart;

import com.khutircraftubackend.user.UserEntity;
import com.khutircraftubackend.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Collection;

import static com.khutircraftubackend.cart.CartItemResponseMessage.CART_ITEM_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CartItemService {

	private final CartItemRepository cartItemRepository;
	private final CartItemMapper cartItemMapper;
	private final UserService userService;

	private UserEntity getUserByEmail(Principal principal) {

		return userService.findByPrincipal(principal);
	}

	public Collection<CartItemResponse> getCartItems(Principal principal) {

		UserEntity user = getUserByEmail(principal);

		Collection<CartItemEntity> cartItems = cartItemRepository.findByUserId(user.getId());

		return cartItemMapper.toCartItemResponse(cartItems);
	}

	public CartItemResponse getCartItemByPriceId(Principal principal, Long productPriceId) {

		UserEntity user = getUserByEmail(principal);

		CartItemEntity cartItem = cartItemRepository.findByUserIdAndProductPriceId(
				user.getId(), productPriceId)
				.orElseThrow(() -> new CartItemNotFoundException(
						String.format(CART_ITEM_NOT_FOUND, productPriceId)));

		return cartItemMapper.toCartItemResponse(cartItem);
	}
}
