package com.ubic.shop.repository;

import com.ubic.shop.domain.Category;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CategoryRepository extends CrudRepository<Category, Long> {

    // Kurly id - 상품 생성할 때 필요함
    @Query("select u from Category u where u.kurlyId = ?1")
    Category findByKurlyId(Long categoryId);

    @Query("select u.id from Category u ")
    List<Long> getAllCategoryId();

}
