package com.khutircraftubackend.bootstrap;

import lombok.experimental.UtilityClass;

import java.util.Map;


@UtilityClass
public class SeedProductImageRegistry {

	public static final Map<String, String> IMAGES = Map.ofEntries(
			// Курятина
			Map.entry("Куряча тушка",    "Курятина тушка.jpg"),
			Map.entry("Куряче серце",    "Курятина серце.jpg"),
			Map.entry("Куряча печінка",  "Курятина печінка.jpg"),
			Map.entry("Курячі стегна",   "Курятина стегна.jpg"),
			Map.entry("Курячі крильця",  "Курятина крильця.jpg"),
			Map.entry("Куряче філе",     "Курятина філе.jpg"),
			Map.entry("Фарш курячий",     "Курятина фарш.jpg"),
			Map.entry("Копчена курка",  "Курятина копчена.jpg"),
			Map.entry("Курка гриль",    "Курятина гриль.jpg"),

			// Свинина
			Map.entry("Свинина",         "Свинина.jpg"),
			Map.entry("Свиняча вирізка", "Свинина вирізка.jpg"),
			Map.entry("Свинячі ребра",   "Свинина ребра.jpg"),
			Map.entry("Свиняча грудинка","Свинина грудинка.jpg"),
			Map.entry("Свиняча печінка", "Свинина печінка.jpg"),
			Map.entry("Бекон",   "Свинина бекон.jpg"),
			Map.entry("Ковбаса", "Свинина ковбаса.jpg"),
			Map.entry("Сало",    "Свинина сало.jpg"),

			// Яловичина
			Map.entry("Яловичина",        "Яловичина.jpg"),
			Map.entry("Яловича вирізка","Яловичина вирізка.jpg"),
			Map.entry("Яловичі ребра",  "Яловичина ребра.jpg"),
			Map.entry("Яловичий фарш",   "Яловичина фарш.jpg"),
			Map.entry("Яловича печінка","Яловичина печінка.jpg"),
			Map.entry("Яловичий язик",   "Яловичина язик.jpg"),
			Map.entry("Стейк яловичий",  "Яловичина стейк.jpg")
	);
}
