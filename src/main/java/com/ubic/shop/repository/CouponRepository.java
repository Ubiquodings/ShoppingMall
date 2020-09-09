package com.ubic.shop.repository;

import com.ubic.shop.domain.CategoryCouponType;
import com.ubic.shop.domain.Coupon;
import com.ubic.shop.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CouponRepository extends CrudRepository<Coupon, Long> {

    List<Coupon> findByUserId(Long userId); // 나의 쿠폰 페이지에서 확인해보기

    @Query("select m from Coupon m where m.product.id in :ids and m.user.id = :userId")
    List<Coupon> findByProductListAndUser(@Param("ids") List<Long> productIds, @Param("userId") long userId);

    @Query("select m from Coupon m where m.id in :ids and m.user.id = :userId")
    List<Coupon> findByUserIdandIds(@Param("ids") List<Long> ids, @Param("userId") long userId);

    // Coupon 에는 product 가 없어 ProductCoupon 에 있지! Repo 를 따로 만들어야 하나 ? 일단 확인해보자 ! 잘된다!
    @Query("select m from Coupon m where m.product.id = :productId and m.user.id = :userId")
    List<Coupon> findByProductAndUser(@Param("productId") long productId, @Param("userId") long userId);

//  @Query(value="select o from Order o where o.user.id = :userId and o.status = :status")
    @Query("select m from Coupon m where m.id = :id and m.user.id = :userId and m.type = :couponType")
    List<Coupon> findByCategoryAndUserAndCouponType(@Param("id") long categoryId, @Param("userId") long userId, @Param("couponType") CategoryCouponType type);
}
