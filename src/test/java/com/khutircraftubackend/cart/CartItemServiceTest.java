package com.khutircraftubackend.cart;

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

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.khutircraftubackend.user.Role.BUYER;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
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
	@DisplayName("createOrUpdateCartItem")
	class CreateOrUpdateCarteItems {

		@Test
		@DisplayName("valid input: returns response built from persisted row")
		void createsOrUpdatesItem_whenValidRequest() {

			Long productPriceId = 2L;
			CartItemRequest request = new CartItemRequest(5);

			when(userService.findByPrincipal(principal)).thenReturn(user);
			doNothing().when(productPriceService).assertProductPriceExists(productPriceId);
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

}
