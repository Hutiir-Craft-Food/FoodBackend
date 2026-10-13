package com.khutircraftubackend.cart;

import com.khutircraftubackend.product.price.service.ProductPriceService;
import com.khutircraftubackend.user.UserEntity;
import com.khutircraftubackend.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.Collection;

import static com.khutircraftubackend.cart.CartItemResponseMessage.CART_ITEM_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CartItemService {

	private final CartItemRepository cartItemRepository;
	private final CartItemMapper cartItemMapper;
	private final UserService userService;
	private final ProductPriceService productPriceService;

	public Collection<CartItemResponse> getCartItems(Principal principal) {

		UserEntity user = userService.findByPrincipal(principal);

		Collection<CartItemEntity> cartItems = cartItemRepository.findByUserId(user.getId());

		return cartItemMapper.toCartItemResponse(cartItems);
	}

	public CartItemResponse getCartItemByPriceId(Principal principal,
												 Long productPriceId) {

		UserEntity user = userService.findByPrincipal(principal);

		CartItemEntity cartItem = cartItemRepository.findByUserIdAndProductPriceId(
						user.getId(), productPriceId)
				.orElseThrow(() -> new CartItemNotFoundException(
						String.format(CART_ITEM_NOT_FOUND, productPriceId)));

		return cartItemMapper.toCartItemResponse(cartItem);
	}

	@Transactional
	public CartItemResponse createOrUpdateCartItem(Principal principal,
												   Long productPriceId,
												   CartItemRequest request) {

		UserEntity user = userService.findByPrincipal(principal);

		productPriceService.assertProductPriceExists(productPriceId);

		cartItemRepository.upsertCartItem(user.getId(), productPriceId, request.quantity());

		CartItemEntity cartItem = cartItemRepository.findByUserIdAndProductPriceId(user.getId(), productPriceId)
				.orElseThrow(() -> new IllegalStateException(
						"Cart item not found right after upsert for productPriceId=%d"
								.formatted(productPriceId)
				));

		return cartItemMapper.toCartItemResponse(cartItem);
	}

	@Transactional
	public void removeCartItem(Principal principal, Long productPriceId) {

		UserEntity user = userService.findByPrincipal(principal);

		productPriceService.assertProductPriceExists(productPriceId);

		int deleted = cartItemRepository.deleteByUserIdAndProductPriceId(user.getId(), productPriceId);

		if (deleted == 0) {
			throw new CartItemNotFoundException(
					String.format(CART_ITEM_NOT_FOUND, productPriceId));
		}
	}
}
