package com.ubic.shop.repository;

import com.ubic.shop.domain.Product;
import com.ubic.shop.domain.ProductCategory;
import com.ubic.shop.dto.ProductResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
public class ProductCategoryRepository {

    private final EntityManager em;

    public void save(ProductCategory productCategory) {
        em.persist(productCategory);
    }

}
