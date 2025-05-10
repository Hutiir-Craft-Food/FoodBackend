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
        select
            pid as id,
            pname as name,
            thumbnail_image as thumbnailImage,
            available as available,
            cid as category_id,
            cname as category_name
        from search_suggestions(:query)
        where tsvector @@ tsquery
        and similarity > 0
        order by ts_rank_cd(tsvector, tsquery) desc, similarity desc
        """,
            countQuery = """
        select count(*)
        from search_suggestions(:query)
        where tsvector @@ tsquery
        and similarity > 0
        """,
            nativeQuery = true
    )
    Page<ProductSearchResult> searchProducts(@Param("query") String query, Pageable pageable);
}

