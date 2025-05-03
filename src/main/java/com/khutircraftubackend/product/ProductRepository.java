package com.khutircraftubackend.product;

import com.khutircraftubackend.product.search.response.ProductSearchResult;
import com.khutircraftubackend.seller.SellerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    
    Page<ProductEntity> findAllBy(Pageable pageable);
    
    Optional<ProductEntity> findProductById(Long id);
    
    void deleteBySeller(SellerEntity seller);
    
    List<ProductEntity> findAllBySeller(SellerEntity currentSeller);
    
    
    @Query(value = """
    with recursive catalogue as (
            -- root categories:
        select
              c.id,
              c.parent_id,
              c.name as self_name,
              c.keywords,
              concat(c.name, ',', coalesce(c.keywords, ''))::text as text
        from categories c
        where parent_id is null

        union all
              -- sub categories:
        select
              c.id,
              c.parent_id,
              c.name as self_name,
              c.keywords,
              concat(ctg.text, ',', clean(c.name), ',', coalesce(c.keywords, ''))::text as text
        from catalogue ctg
        inner join categories c on c.parent_id = ctg.id
    ), suggestions as (
        select
             p.id as pid,
             p.category_id as pcid,
             p.name as pname,
             p.thumbnail_image,
             p.available,
             c.id as cid,
             c.self_name as cname,
            to_tsvector('simple', concat(clean(p.name), ' ', coalesce(c.text, ''))) as tsvector,
            case
                when length(:query) < 3
                    then to_tsquery('simple', :query || ':*')
                else to_tsquery('simple', regexp_replace(clean(:query),'\s+', ' & ', 'g') || ':*')
            end as tsquery,
            greatest(
              similarity(clean(p.name), clean(:query)),
              similarity(clean(c.self_name), clean(:query)),
              similarity(clean(coalesce(c.keywords, '')), clean(:query)),
              similarity(clean(coalesce(c.text, '')), clean(:query))
            ) as similarity,
            c.keywords
        from products p
        inner join catalogue c on c.id = p.category_id
        where p.available = true
    )
        select
            s.pid as id,
            s.pname as name,
            s.thumbnail_image as thumbnailImage,
            s.available as available,
            s.cid as category_id,
            s.cname as category_name
        from suggestions s
        where 1=1
        and tsvector @@ s.tsquery
        and similarity > 0
        order by ts_rank_cd(s.tsvector, s.tsquery) desc, s.similarity desc
        limit :limit offset :offset;
    """, nativeQuery = true)
    List<ProductSearchResult> searchProducts(@Param("query") String query,
                                             @Param("limit") int limit,
                                             @Param("offset") int offset);
    
    @Query(value = """
    with recursive catalogue as (
        select
              c.id,
              c.parent_id,
              c.name as self_name,
              c.keywords,
              concat(c.name, ',', coalesce(c.keywords, ''))::text as text
        from categories c
        where parent_id is null
        union all
        select
              c.id,
              c.parent_id,
              c.name as self_name,
              c.keywords,
              concat(ctg.text, ',', clean(c.name), ',', coalesce(c.keywords, ''))::text as text
        from catalogue ctg
        inner join categories c on c.parent_id = ctg.id
    ), suggestions as (
        select
             p.id as pid,
             p.name as pname,
             to_tsvector('simple', concat(clean(p.name), ' ', coalesce(c.text, ''))) as tsvector,
             case
                 when length(:query) < 3
                     then to_tsquery('simple', :query || ':*')
                 else to_tsquery('simple', regexp_replace(clean(:query),'\s+', ' & ', 'g') || ':*')
             end as tsquery,
             greatest(
               similarity(clean(p.name), clean(:query)),
               similarity(clean(c.self_name), clean(:query)),
               similarity(clean(coalesce(c.keywords, '')), clean(:query)),
               similarity(clean(coalesce(c.text, '')), clean(:query))
             ) as similarity
        from products p
        inner join catalogue c on c.id = p.category_id
        where p.available = true
    )
    select count(*)
    from suggestions s
    where s.tsvector @@ s.tsquery
      and s.similarity > 0
    """, nativeQuery = true)
    long countProducts(@Param("query") String query);
}

