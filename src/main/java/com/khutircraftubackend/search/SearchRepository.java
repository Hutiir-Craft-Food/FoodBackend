package com.khutircraftubackend.search;

import com.khutircraftubackend.product.ProductEntity;
import com.khutircraftubackend.search.response.ProductSearchView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchRepository extends JpaRepository<ProductEntity, Long> {
    @Query(value = """
        with q as (
            select :query as query
        ), s as (
            select
              c.id as category_id,
              c.name as category_name,
              p.id as product_id,
              p.name as product_name,
              p.thumbnail_image,
              p.available,
              c.path,
              c.keywords,
              to_tsvector(p.name) || to_tsvector(c.keywords) as tsvector,
              to_tsquery('simple', replace(clean(q.query) || ':*', ' ', '|')) as tsquery,
              similarity(clean(q.query), concat(clean(p.name), '/', c.keywords)) as similarity,
              word_similarity(clean(q.query), concat(clean(p.name), '/', c.keywords)) as word_similarity,
              clean(q.query) <<-> concat(clean(p.name), '/', c.keywords) as distance
          from products p
          left join v_categories c
              on c.id = p.category_id
          cross join q
          where p.available = true
      )
      select
          s.*,
          ts_rank(s.tsvector, s.tsquery) as rank,
          s.tsvector @@ s.tsquery as is_matching
      from s
      where s.tsvector @@ s.tsquery or (
          s.similarity > 0.1
          and word_similarity >= 0.3
          and distance < 0.7
      )
      order by rank desc, s.similarity desc, s.product_name
      limit 50;
    """, nativeQuery = true)
    List<ProductSearchView> searchProducts(@Param("query") String query);
}
