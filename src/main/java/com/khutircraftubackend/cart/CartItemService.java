package com.khutircraftubackend.cart;

import com.khutircraftubackend.cart.exception.CartBusyException;
import com.khutircraftubackend.cart.exception.CartItemNotFoundException;
import com.khutircraftubackend.cart.exception.CartLimitExceededException;
import com.khutircraftubackend.cart.request.CartItemRequest;
import com.khutircraftubackend.cart.request.CartMergeItemRequest;
import com.khutircraftubackend.cart.response.CartItemResponse;
import com.khutircraftubackend.product.price.service.ProductPriceService;
import com.khutircraftubackend.user.UserEntity;
import com.khutircraftubackend.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

import static com.khutircraftubackend.cart.exception.CartItemResponseMessage.*;

@Service
@RequiredArgsConstructor
public class CartItemService {

	private final CartItemRepository cartItemRepository;
	private final CartItemMapper cartItemMapper;
	private final UserService userService;
	private final ProductPriceService productPriceService;

	private static final int MAX_UNIQUE_ITEMS = 49;

	public Collection<CartItemResponse> getCartItems(Principal principal) {

		UserEntity user = userService.findByPrincipal(principal);

		Collection<CartItemEntity> cartItems = cartItemRepository.findByUserId(user.getId());

		return cartItemMapper.toCartItemResponse(cartItems);
	}

	@Transactional
	public Collection<CartItemResponse> mergeCartItems(Principal principal,
													   List<CartMergeItemRequest> request) {

		UserEntity user;
		try {
			user = userService.findByPrincipalForUpdate(principal);
		} catch (PessimisticLockingFailureException e) {
			throw new CartBusyException(CART_BEING_UPDATE, e);
		}

		Map<Long, Integer> requestedQuantities = request.stream()
				.collect(Collectors.toMap(
						CartMergeItemRequest::productPriceId,
						CartMergeItemRequest::quantity,
						Math::max,
						LinkedHashMap::new
				));

		if (!requestedQuantities.isEmpty()) {
			Set<Long> existingPriceIds = Set.copyOf(
					productPriceService.findExistingPriceIds(new ArrayList<>(requestedQuantities.keySet())));

			requestedQuantities.forEach((productPriceId, quantity) -> {

				if (existingPriceIds.contains(productPriceId)) {
					cartItemRepository.mergeCartItem(user.getId(), productPriceId, quantity);
				}
			});
		}

		Collection<CartItemEntity> mergedCart = cartItemRepository.findByUserId(user.getId());

		return cartItemMapper.toCartItemResponse(mergedCart);
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

		boolean itemExists = cartItemRepository.existsByUserIdAndProductPriceId(user.getId(), productPriceId);

		if (!itemExists) {
			long currentUniqueCount = cartItemRepository.countDistinctProductPriceIdsByUserId(user.getId());

			if (currentUniqueCount >= MAX_UNIQUE_ITEMS) {
				throw new CartLimitExceededException(
						String.format(CART_LIMIT_EXCEEDED, MAX_UNIQUE_ITEMS, currentUniqueCount),
						currentUniqueCount
				);
			}
		}

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
