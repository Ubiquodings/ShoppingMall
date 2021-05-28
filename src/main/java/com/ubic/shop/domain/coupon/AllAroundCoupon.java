package com.ubic.shop.domain.coupon;

import com.ubic.shop.domain.User;
import lombok.*;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("allaround")
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor
public class AllAroundCoupon extends Coupon {

    @Builder
    public AllAroundCoupon(String name, User user, int discountRate) {
        super(name, user, discountRate, CouponType.all_around);
    }

}
