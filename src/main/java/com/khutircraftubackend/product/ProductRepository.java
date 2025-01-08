package com.khutircraftubackend.product;

import com.khutircraftubackend.seller.SellerEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
	
	List<ProductEntity> findAllBy(Pageable pageable);
	
	Optional<ProductEntity> findProductById(Long id);
	
	void deleteBySeller(SellerEntity seller);
	
	List<ProductEntity> findAllBySeller(SellerEntity currentSeller);
	
	/**
	 * Пошук здійснюється з пріоритетом:
	 * 1. Спочатку шукаємо за назвою категорії.
	 * 2. Якщо не знайдено, шукаємо за назвою продукту.
	 * 3. Якщо не знайдено, шукаємо за описом продукту.
	 *
	 * @param keyword Ключове слово для пошуку. Пошук здійснюється за цим словом в трьох полях.
	 * @return Список продуктів (List<ProductEntity>), що відповідають критеріям пошуку, відсортований за пріоритетом.
	 *
	 * Використання to_tsvector та to_tsquery залежить від локалі бази даних (uk_UA.UTF-8).
	 */
	@Query(value = """
			    SELECT p.*
			    FROM products p
			    JOIN categories c ON p.category_id = c.id
			    WHERE
			        to_tsvector('simple', c.name) @@ to_tsquery('simple', :keyword) OR
			        to_tsvector('simple', p.name) @@ to_tsquery('simple', :keyword)
			    ORDER BY
			        CASE
			            WHEN to_tsvector('simple', c.name) @@ to_tsquery('simple', :keyword) THEN 1
			            WHEN to_tsvector('simple', p.name) @@ to_tsquery('simple', :keyword) THEN 2
			        END
			""", nativeQuery = true)
	List<ProductEntity> searchWithPriority(@Param("keyword") String keyword);
	
	/**
	 * Здійснює пошук підказок для автозаповнення.
	 * Пошук виконується з ігноруванням регістру серед назв продуктів, категорій та описів.
	 * Результати унікальні, відсортовані за назвою, з обмеженням до 10 записів.
	 *
	 * @param query Ключове слово для пошуку. Використовується для пошуку збігів, що починаються з введеного слова.
	 * @return Список унікальних рядків, що відповідають критеріям пошуку, відсортованих за алфавітом.
	 *
	 * Використання ILIKE залежить від локалі бази даних (uk_UA.UTF-8).
	 */
	@Query(value = """
			    SELECT DISTINCT name
			             FROM (
			                 SELECT p.name AS name
			                 FROM products p
			                 JOIN categories c ON p.category_id = c.id
			                 WHERE c.name ILIKE (CONCAT('%', :query, '%')) OR
			                       p.name ILIKE (CONCAT('%', :query, '%'))
			             ) suggestions /*тимчасова таблиця*/
			             ORDER BY name
			             LIMIT 10;
			""", nativeQuery = true)
	List<String> findSuggestions(@Param("query") String query);
}

