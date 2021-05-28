package com.ubic.shop.repository;

import com.ubic.shop.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends CrudRepository<Product, Long> {

    @Override
    @Query("select m from Product m left join fetch m.category where m.id = :productId")
    Optional<Product> findById(@Param("productId") Long id);

    List<Product> findByName(String name);

    @Query(value = "select * from Product limit :limit", nativeQuery = true)
    List<Product> findProductsByLimit(@Param("limit") Long limit);

    // 카운트 쿼리 분리
    @Query(value = "select p from Product p",
            countQuery = "select count(p.name) from Product p")
    Page<Product> findProductsCountBy(Pageable pageable);

    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
    // 패치조인하면 페이징 못한다

    long countByCategoryId(Long categoryId);

    @Query(value = "select p from Product p where :results in p.ProductTagList")
    List<Product> findBystemmingResults(@Param("results") String stemmingResults);

    @Query(value = "select p from Product p where p.id in :productIdList")
    Page<Product> findByProductIdList(@Param("productIdList") List<Long> productIdList, Pageable pageable);

    @Query(value = "select p from Product p where p.id in :productIdList")
    List<Product> findByProductIdListNoPage(@Param("productIdList") List<Long> productIdList);

}
