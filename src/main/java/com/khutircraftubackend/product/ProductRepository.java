package com.khutircraftubackend.product;

import com.khutircraftubackend.search.ProductSearchResult;
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
    
    @Query(value = """
    with recursive catalogue as (
            -- root categories:
        select
              c.id,
              concat(c.name, ',', coalesce(c.keywords, ''))::text as "text"
        from categories c
        where parent_id is null
        
        union all
              -- sub categories:
        select
              c.id,
              concat(ctg.text, ',', c.name, ',', coalesce(c.keywords, ''))::text as "text"
        from catalogue ctg
        inner join categories c on c.parent_id = ctg.id
    ), suggestions as (
        select
            distinct p.id, p.name,
            to_tsvector('simple', concat(p.name, ' ', coalesce(c."text", ''))) as "tsvector",
            CASE
                WHEN length(trim(regexp_replace(:query, '[^a-zA-Zа-яА-ЯїЇєЄґҐ\\d\\s]', '', 'g'))) = 0
                    THEN NULL
                ELSE to_tsquery('simple', regexp_replace(regexp_replace(:query, '[^a-zA-Zа-яА-ЯїЇєЄґҐ\\d\\s]', '', 'g'),'\\s+', ':* & ', 'g') || ':*')
            END as "tsquery",
            similarity(p.name, :query) as "similarity"
        from products p
        inner join catalogue c on c.id = p.category_id
        where p.available = true
    )
        select
            s.id, s.name,
            ts_rank_cd("tsvector", "tsquery") as rank,
            "similarity"
        from suggestions s
        where "tsvector" @@ "tsquery" or "similarity" > 0.3
        order by rank desc, similarity desc
        limit 50;
    """, nativeQuery = true)
    List<ProductSearchResult> searchProducts(@Param("query") String query);
    
}

