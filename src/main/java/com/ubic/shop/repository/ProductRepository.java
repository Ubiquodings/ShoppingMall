package com.ubic.shop.repository;

import com.ubic.shop.domain.Product;
import com.ubic.shop.domain.Product;
import com.ubic.shop.dto.ProductResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

//@Repository
//@RequiredArgsConstructor
public interface ProductRepository extends CrudRepository<Product, Long> {

    List<Product> findByName(String name);

    @Query(value="select * from Product limit 40", nativeQuery = true)
    List<Product> findDefaultProducts();

    List<Product> findByCategoryId(Long id);
//    List<Product> findAll();
//    private final EntityManager em;
//
//    public ProductResponseDto save(Product product) {
//        if (product.getId() == null) {
//            em.persist(product);
//        }/* else { // 변경 감지로 사용하라고 했다
//            em.merge(product);
//        }*/
//        return new ProductResponseDto(product); // 여기서는 product 반환하고, 컨트롤러에서 new 하면 되지 않나 ?
//    }
//
//    public Product findOne(Long id) {
//        return em.find(Product.class, id);
//    }
//
//    public List<Product> findAll() {
//        return em.createQuery("select i from Product i",Product.class).getResultList();
//    }
//
//    public List<Product> findByName(String name) {
//        return em.createQuery("select m from Product m where m.name = :name", Product.class)
//                .setParameter("name", name)
//                .getResultList()/*.get(0)*/;
//
//    }
}
