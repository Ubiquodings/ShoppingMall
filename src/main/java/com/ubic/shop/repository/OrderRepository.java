package com.ubic.shop.repository;

import com.ubic.shop.domain.Order;
import com.ubic.shop.domain.Order;
import com.ubic.shop.domain.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    public List<Order> findAll(Long userId) {
        return em.createQuery("select i from Order i where user_id=:userId", Order.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public List<Order> findAllOrdered(Long userId) {
        return em.createQuery("select i from Order i where user_id=:userId and status=:orderStatus", Order.class)
                .setParameter("userId", userId)
                .setParameter("orderStatus", OrderStatus.ORDER)
                .getResultList();
    }
}
