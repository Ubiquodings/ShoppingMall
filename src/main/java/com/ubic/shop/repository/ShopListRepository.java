package com.ubic.shop.repository;

import com.ubic.shop.domain.ShopList;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ShopListRepository extends CrudRepository<ShopList, Long> {

    List<ShopList> findAllByUserId(Long userId);

//    @Query("delete from ShopList m where m.user.id = :userId") // not support
//    void deleteByIds(@Param("userId") Long shopListUserId);

//    @Query("select m.id from ShopList m")
//    List<Long> findUsernameList();

//    private final EntityManager em;
//
//    public void save(ShopList shopList) {
//        em.persist(shopList);
//    }
//
//    public ShopList findOne(Long id) {
//        return em.find(ShopList.class, id);
//    }
//
//    public List<ShopList> findAll(Long userId){
//        return em.createQuery("select i from ShopList i where user_id=:userId", ShopList.class)
//                .setParameter("userId",userId)
//                .getResultList();
//    }
}
