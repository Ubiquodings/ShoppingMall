package com.ubic.shop.repository;

import com.ubic.shop.domain.Order;
import com.ubic.shop.domain.OrderStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends CrudRepository<Order, Long> {

    List<Order> findByUserId(Long userId);

    @Query(value = "select o from Order o where o.user.id = :userId and o.status = :status")
    List<Order> findByUserIdAndOrderStatus(@Param("userId") Long userId, @Param("status") OrderStatus status);

}
