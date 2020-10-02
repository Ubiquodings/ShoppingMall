package com.ubic.shop.domain.user_number;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@NoArgsConstructor
@Entity
@Getter
@Setter(AccessLevel.PROTECTED)
public class CouponUseUserNumber {
    String couponType; long userNumber;

    @Id
    @GeneratedValue
    @Column(name = "coupon_use_user_number_id")
    private Long id;


    @Builder
    public CouponUseUserNumber(String couponType, long userNumber) {
        this.couponType = couponType;
        this.userNumber = userNumber;
    }

    public void changeUserNumber(long userNumber){
        this.userNumber = userNumber;
    }

}
