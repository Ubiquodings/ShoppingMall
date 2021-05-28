package com.ubic.shop.repository.user_number;

import com.ubic.shop.domain.user_number.CouponUseUserNumber;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CouponUseUserNumberRepository extends CrudRepository<CouponUseUserNumber, Long> {

    List<CouponUseUserNumber> findByCouponType(String couponType);
}
