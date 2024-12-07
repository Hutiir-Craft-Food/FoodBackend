package com.khutircraftubackend.delivery;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum DeliveryMethodProvider {
	NOVA_POSHTA("Нова Пошта"),
	UKRPOSHTA("УкрПошта"),
	COURIER("Кур'єр"),
	OTHER("Інше");
	
	private final String displayName;
}
