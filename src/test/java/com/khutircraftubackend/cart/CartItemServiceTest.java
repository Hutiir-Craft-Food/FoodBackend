package com.khutircraftubackend.cart;

import com.khutircraftubackend.cart.exception.CartBusyException;
import com.khutircraftubackend.cart.exception.CartItemNotFoundException;
import com.khutircraftubackend.cart.exception.CartLimitExceededException;
import com.khutircraftubackend.cart.request.CartItemRequest;
import com.khutircraftubackend.cart.request.CartMergeItemRequest;
import com.khutircraftubackend.cart.response.CartItemResponse;
import com.khutircraftubackend.product.ProductEntity;
import com.khutircraftubackend.product.exception.ProductPriceNotFoundException;
import com.khutircraftubackend.product.price.entity.ProductPriceEntity;
import com.khutircraftubackend.product.price.service.ProductPriceService;
import com.khutircraftubackend.user.UserEntity;
import com.khutircraftubackend.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.CannotAcquireLockException;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.khutircraftubackend.user.Role.BUYER;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartItemServiceTest {

	private final UserEntity user = new UserEntity(1L, "test@user.com", "pass", BUYER, true, false, LocalDateTime.now());
	private final Principal principal = user::getEmail;
	@Mock
	private CartItemRepository cartItemRepository;
	@Mock
	private ProductPriceService productPriceService;
	@Mock
	private UserService userService;
	@Spy
	private CartItemMapper cartItemMapper = Mappers.getMapper(CartItemMapper.class);
	@InjectMocks
	private CartItemService cartItemService;

	@Nested
	@DisplayName("mergeCartItems")
	class MergeCartItems {

		@Test
		void mergesExistingPricesAndOmitsStalePricesWithoutApplyingTheCap() {
			List<CartMergeItemRequest> request = List.of(
					new CartMergeItemRequest(2L, 5),
					new CartMergeItemRequest(3L, 8),
					new CartMergeItemRequest(99L, 4),
					new CartMergeItemRequest(2L, 7)
			);
			CartItemEntity existingItem = cartItem(2L, 10);
			CartItemEntity newItem = cartItem(3L, 8);
			// Недоступний товар лишається у відповіді merge: позначку "більше недоступний"
			// фронтенд отримує пізніше з GET /v1/products/variants
			ProductEntity unavailableProduct = new ProductEntity();
			unavailableProduct.setAvailable(false);
			newItem.getProductPrice().setProduct(unavailableProduct);

			when(userService.findByPrincipalForUpdate(principal)).thenReturn(user);
			when(productPriceService.findExistingPriceIds(List.of(2L, 3L, 99L)))
					.thenReturn(List.of(2L, 3L));
			doNothing().when(cartItemRepository).mergeCartItem(user.getId(), 2L, 7);
			doNothing().when(cartItemRepository).mergeCartItem(user.getId(), 3L, 8);
			when(cartItemRepository.findByUserId(user.getId()))
					.thenReturn(List.of(existingItem, newItem));

			Collection<CartItemResponse> response = cartItemService.mergeCartItems(principal, request);

			assertEquals(List.of(2L, 3L), response.stream()
					.map(CartItemResponse::productPriceId)
					.toList());
			assertEquals(List.of(10, 8), response.stream()
					.map(CartItemResponse::quantity)
					.toList());
			verify(cartItemRepository, never()).countDistinctProductPriceIdsByUserId(anyLong());
			verify(cartItemRepository, never()).mergeCartItem(user.getId(), 99L, 4);
		}

		@Test
		void emptyGuestCartReturnsCurrentCartAndStillLocksUser() {
			CartItemEntity existingItem = cartItem(2L, 4);
			when(userService.findByPrincipalForUpdate(principal)).thenReturn(user);
			when(cartItemRepository.findByUserId(user.getId())).thenReturn(List.of(existingItem));

			Collection<CartItemResponse> response = cartItemService.mergeCartItems(principal, List.of());

			assertEquals(List.of(2L), response.stream()
					.map(CartItemResponse::productPriceId)
					.toList());
			verify(userService).findByPrincipalForUpdate(principal);
			verifyNoInteractions(productPriceService);
			verify(cartItemRepository, never()).mergeCartItem(anyLong(), anyLong(), anyInt());
		}

		@Test
		void returnsAllEntriesWhenMergedCartExceedsUniqueItemCap() {
			List<CartItemEntity> overCapCart = java.util.stream.IntStream.rangeClosed(1, 50)
					.mapToObj(id -> cartItem((long) id, 1))
					.toList();
			when(userService.findByPrincipalForUpdate(principal)).thenReturn(user);
			when(cartItemRepository.findByUserId(user.getId())).thenReturn(overCapCart);

			Collection<CartItemResponse> response = cartItemService.mergeCartItems(principal, List.of());

			assertEquals(50, response.size());
		}

		@Test
		void retryingSameMergeDoesNotIncreaseRequestedQuantity() {
			List<CartMergeItemRequest> request = List.of(new CartMergeItemRequest(2L, 5));
			when(userService.findByPrincipalForUpdate(principal)).thenReturn(user);
			when(productPriceService.findExistingPriceIds(List.of(2L))).thenReturn(List.of(2L));
			doNothing().when(cartItemRepository).mergeCartItem(user.getId(), 2L, 5);
			when(cartItemRepository.findByUserId(user.getId())).thenReturn(List.of(cartItem(2L, 5)));

			CartItemResponse firstResult = cartItemService.mergeCartItems(principal, request).iterator().next();
			CartItemResponse retryResult = cartItemService.mergeCartItems(principal, request).iterator().next();

			assertEquals(firstResult.quantity(), retryResult.quantity());
			verify(cartItemRepository, times(2)).mergeCartItem(user.getId(), 2L, 5);
		}

		@Test
		void persistenceFailurePropagatesForTransactionalRollback() {
			when(userService.findByPrincipalForUpdate(principal)).thenReturn(user);
			when(productPriceService.findExistingPriceIds(List.of(2L))).thenReturn(List.of(2L));
			doThrow(new IllegalStateException("persistence failed"))
					.when(cartItemRepository).mergeCartItem(user.getId(), 2L, 5);

			assertThatThrownBy(() -> cartItemService.mergeCartItems(
					principal, List.of(new CartMergeItemRequest(2L, 5))))
					.isInstanceOf(IllegalStateException.class)
					.hasMessage("persistence failed");

			verify(cartItemRepository, never()).findByUserId(user.getId());
		}

		@Test
		void lockTimeoutIsTranslatedToCartBusy() {
			when(userService.findByPrincipalForUpdate(principal))
					.thenThrow(new CannotAcquireLockException("lock timeout"));

			assertThatThrownBy(() -> cartItemService.mergeCartItems(
					principal, List.of(new CartMergeItemRequest(2L, 5))))
					.isInstanceOf(CartBusyException.class);

			verifyNoInteractions(cartItemRepository);
		}


		private CartItemEntity cartItem(Long productPriceId, int quantity) {
			ProductPriceEntity price = new ProductPriceEntity();
			price.setId(productPriceId);
			CartItemEntity item = new CartItemEntity();
			item.setProductPrice(price);
			item.setQuantity(quantity);
			return item;
		}


	}

	@Nested
	@DisplayName("createOrUpdateCartItem")
	class CreateOrUpdateCarteItems {

		@Test
		@DisplayName("valid input: returns response built from persisted row")
		void createsOrUpdatesItem_whenValidRequest() {

			Long productPriceId = 2L;
			CartItemRequest request = new CartItemRequest(5);

			when(userService.findByPrincipal(principal)).thenReturn(user);
			doNothing().when(productPriceService).assertProductPriceExists(productPriceId);
			when(cartItemRepository.findByUserIdAndProductPriceId(user.getId(), productPriceId))
					.thenReturn(Optional.empty());
			when(cartItemRepository.countDistinctProductPriceIdsByUserId(user.getId())).thenReturn(10L);
			doNothing().when(cartItemRepository).upsertCartItem(user.getId(), productPriceId, 5);

			ProductPriceEntity price = new ProductPriceEntity();
			price.setId(productPriceId);
			CartItemEntity persisted = new CartItemEntity();
			persisted.setProductPrice(price);
			persisted.setQuantity(5);

			when(cartItemRepository.findByUserIdAndProductPriceId(user.getId(), productPriceId))
					.thenReturn(Optional.of(persisted));

			CartItemResponse response = cartItemService.createOrUpdateCartItem(principal, productPriceId, request);

			assertThat(response.productPriceId()).isEqualTo(productPriceId);
			assertThat(response.quantity()).isEqualTo(5);

			InOrder order = inOrder(productPriceService, cartItemRepository);
			order.verify(productPriceService).assertProductPriceExists(productPriceId);
			order.verify(cartItemRepository).upsertCartItem(user.getId(), productPriceId, 5);
		}

		@Test
		@DisplayName("insert branch: rejects when unique items count is already at the cap")
		void throwsCartLimitExceeded_whenInsertingAtOrAboveUniqueCap() {
			Long productPriceId = 99L;
			CartItemRequest request = new CartItemRequest(1);

			when(userService.findByPrincipal(principal)).thenReturn(user);
			doNothing().when(productPriceService).assertProductPriceExists(productPriceId);
			when(cartItemRepository.existsByUserIdAndProductPriceId(user.getId(), productPriceId)).thenReturn(Boolean.FALSE);
			when(cartItemRepository.countDistinctProductPriceIdsByUserId(user.getId())).thenReturn(49L);

			assertThatThrownBy(() -> cartItemService.createOrUpdateCartItem(principal, productPriceId, request))
					.isInstanceOf(CartLimitExceededException.class)
					.hasMessageContaining("49")
					.hasMessageContaining("49");

			verify(cartItemRepository, never()).upsertCartItem(anyLong(), anyLong(), anyInt());
		}

		@Test
		@DisplayName("update branch: existing item is allowed even when cart is at limit")
		void allowsExistingItemUpdate_evenWhenUniqueCountAtLimit() {
			Long productPriceId = 7L;
			CartItemRequest request = new CartItemRequest(12);

			when(userService.findByPrincipal(principal)).thenReturn(user);
			doNothing().when(productPriceService).assertProductPriceExists(productPriceId);
			when(cartItemRepository.existsByUserIdAndProductPriceId(user.getId(), productPriceId)).thenReturn(Boolean.TRUE);
			doNothing().when(cartItemRepository).upsertCartItem(user.getId(), productPriceId, 12);

			ProductPriceEntity price = new ProductPriceEntity();
			price.setId(productPriceId);
			CartItemEntity persisted = new CartItemEntity();
			persisted.setProductPrice(price);
			persisted.setQuantity(12);
			when(cartItemRepository.findByUserIdAndProductPriceId(user.getId(), productPriceId))
					.thenReturn(Optional.of(persisted));

			CartItemResponse response = cartItemService.createOrUpdateCartItem(principal, productPriceId, request);

			assertThat(response.productPriceId()).isEqualTo(productPriceId);
			assertThat(response.quantity()).isEqualTo(12);
			verify(cartItemRepository, never()).countDistinctProductPriceIdsByUserId(anyLong());
		}

		@Test
		@DisplayName("missing ProductPrice: propagates 404, never touches cart_items")
		void throwsProductPriceNotFound_whenPriceMissing() {

			Long productPriceId = 999L;
			when(userService.findByPrincipal(principal)).thenReturn(user);
			doThrow(new ProductPriceNotFoundException("Ціна відсутня для: 999"))
					.when(productPriceService).assertProductPriceExists(productPriceId);

			assertThatThrownBy(() ->
					cartItemService.createOrUpdateCartItem(principal, productPriceId, new CartItemRequest(1)))
					.isInstanceOf(ProductPriceNotFoundException.class);

			verifyNoInteractions(cartItemRepository);
		}

		@Test
		@DisplayName("scoping: userId завжди резолвиться з Principal, а не з тіла запиту")
		void scopesToAuthenticatedUser_notToRequestBody() {

			UserEntity otherUser = new UserEntity(2L, "other@user.com", "pass", BUYER, true, false, LocalDateTime.now());
			Long productPriceId = 2L;

			when(userService.findByPrincipal(principal)).thenReturn(user);
			doNothing().when(productPriceService).assertProductPriceExists(productPriceId);
			when(cartItemRepository.findByUserIdAndProductPriceId(eq(user.getId()), eq(productPriceId)))
					.thenReturn(Optional.of(new CartItemEntity()));

			cartItemService.createOrUpdateCartItem(principal, productPriceId, new CartItemRequest(1));

			verify(cartItemRepository).upsertCartItem(eq(user.getId()), eq(productPriceId), anyInt());
			verify(cartItemRepository, never()).upsertCartItem(eq(otherUser.getId()), any(), anyInt());
		}


		@Test
		@DisplayName("createOrUpdateCartItem_throwsIllegalState_whenRowMissingAfterUpsert")
		void createOrUpdateCartItem_throwsIllegalState_whenRowMissingAfterUpsert() {

			Long productPriceId = 42L;

			when(userService.findByPrincipal(principal)).thenReturn(user);

			doNothing().when(productPriceService).assertProductPriceExists(productPriceId);
			doNothing().when(cartItemRepository).upsertCartItem(user.getId(), productPriceId, 3);

			when(cartItemRepository.findByUserIdAndProductPriceId(user.getId(), productPriceId))
					.thenReturn(Optional.empty());

			CartItemRequest request = new CartItemRequest(3);

			assertThatThrownBy(() -> cartItemService.createOrUpdateCartItem(principal, productPriceId, request))
					.isInstanceOf(IllegalStateException.class)
					.hasMessageContaining("productPriceId=42");

			verify(cartItemRepository).upsertCartItem(user.getId(), productPriceId, 3);
			verify(cartItemMapper, never()).toCartItemResponse(any(CartItemEntity.class));

		}
	}

	@Nested
	@DisplayName("removeCartItem")
	class RemoveCartItem {

		@Test
		@DisplayName("scoping: userId береться з Principal")
		void deletesOnlyForAuthenticatedUser() {

			Long productPriceId = 2L;
			when(userService.findByPrincipal(principal)).thenReturn(user);
			when(cartItemRepository.deleteByUserIdAndProductPriceId(user.getId(), productPriceId)).thenReturn(1);

			cartItemService.removeCartItem(principal, productPriceId);

			verify(cartItemRepository).deleteByUserIdAndProductPriceId(user.getId(), productPriceId);
			verifyNoMoreInteractions(cartItemRepository);
		}

		@Test
		@DisplayName("не існуюча позиція: CartItemNotFoundException")
		void throwsCartItemNotFound_whenItemAbsent() {

			when(userService.findByPrincipal(principal)).thenReturn(user);
			when(cartItemRepository.deleteByUserIdAndProductPriceId(user.getId(), 99L)).thenReturn(0);

			assertThatThrownBy(() -> cartItemService.removeCartItem(principal, 99L))
					.isInstanceOf(CartItemNotFoundException.class)
					.hasMessageContaining("99");
		}
	}

}
