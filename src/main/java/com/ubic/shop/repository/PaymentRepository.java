package com.ubic.shop.repository;

import com.ubic.shop.domain.Payment;
import com.ubic.shop.domain.ShopList;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PaymentRepository extends CrudRepository<Payment, Long> {

    List<Payment> findAllByUserId(Long userId);
}
