package com.ubic.shop.repository;

import com.ubic.shop.domain.Product;
import com.ubic.shop.domain.ShopList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ShopListRepository {

    private final EntityManager em;

    public void save(ShopList shopList) {
        em.persist(shopList);
    }

    public ShopList findOne(Long id) {
        return em.find(ShopList.class, id);
    }

    public List<ShopList> findAll(Long userId){
        return em.createQuery("select i from ShopList i where user_id=:userId", ShopList.class)
                .setParameter("userId",userId)
                .getResultList();
    }
}
