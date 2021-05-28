package com.ubic.shop.repository;

import com.ubic.shop.domain.Payment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PaymentRepository extends CrudRepository<Payment, Long> {

    List<Payment> findAllByUserId(Long userId);

    @Query("select m from Payment m where m.product.id = :productId and m.user.id = :userId and m.count = :count")
    List<Payment> findByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId, @Param("count") long count);
}
