package com.ubic.shop.repository;

import com.ubic.shop.domain.Coupon;
import com.ubic.shop.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CouponRepository extends CrudRepository<Coupon, Long> {

    List<Coupon> findByUserId(Long userId); // 나의 쿠폰 페이지에서 확인해보기

    @Query("select distinct m from Coupon m where m.product.id in :ids")
    List<Coupon> findByIds(@Param("ids") List<Long> productIds);
}
