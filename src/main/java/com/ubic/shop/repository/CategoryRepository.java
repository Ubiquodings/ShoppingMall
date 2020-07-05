package com.ubic.shop.repository;

import com.ubic.shop.domain.Category;
import com.ubic.shop.domain.ProductCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

//@Repository
//@RequiredArgsConstructor
public interface CategoryRepository extends CrudRepository<Category, Long> {

    @Query("select u from Category u where u.kurlyId = ?1")
    Optional<Category> findByKurlyId(Long categoryId);

//    private final EntityManager em;
//
//    public Category save(Category category) {
//        em.persist(category);
//        return category;
//    }
//
//    // List 반환안하면 null 오류 너무 자주 마주친다!
//    public List<Category> findByName(String name) {
//        return em.createQuery("select m from Category m where m.name = :name", Category.class)
//                .setParameter("name", name)
//                .getResultList()/*.get(0)*/;
//    }
}
