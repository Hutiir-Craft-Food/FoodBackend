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
        --- search results:
        with query as (
            select :query as text
        ), catalogue as (
            select
                c.category_id,
                c.category_name,
                c.product_id,
                c.product_name,
                c.thumbnail_image,
                c.keywords,
                c.available,
                c.tsvector,
                case
                    when length(q.text) < 3
                        then to_tsquery('simple', q.text || ':*')
                        else to_tsquery('simple', replace(clean(q.text), ' ', '&') || ':*')
                end as "tsquery",
                greatest(
                    similarity(clean(c.category_name), clean(q.text)),
                    similarity(c.keywords, clean(q.text))
                ) as similarity
            from v_products c
            full outer join query q on 1 = 1
        )
        select
            s.*, ts_rank_cd(s."tsvector", s."tsquery") as rank
        from catalogue s
        where 1=1
        and s."tsvector" @@ s."tsquery"
        and s."similarity" > 0
        order by rank desc, s.similarity desc
        limit 50
        """, nativeQuery = true)
    List<ProductSearchView> searchProducts(@Param("query") String query);
}
