package com.ubic.shop.repository;

import com.ubic.shop.domain.Coupon;
import com.ubic.shop.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CouponRepository extends CrudRepository<Coupon, Long> {

    List<Coupon> findByUserId(Long userId); // 나의 쿠폰 페이지에서 확인해보기
}
