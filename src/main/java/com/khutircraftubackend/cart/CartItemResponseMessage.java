package com.khutircraftubackend.cart;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CartItemResponseMessage {

	public static final String CART_ITEM_NOT_FOUND = "Товар %d у кошику не знайдено";
	public static final String CART_LIMIT_EXCEEDED = "Кошик досяг максимального ліміту у %d унікальних позицій. Поточна кількість: %d";
}
