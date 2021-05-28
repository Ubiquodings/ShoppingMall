package com.ubic.shop.domain.coupon;


import com.ubic.shop.domain.BaseTimeEntity;
import com.ubic.shop.domain.User;
import lombok.*;

import javax.persistence.*;

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Entity
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Coupon extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "coupon_id")
    private Long id;
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private int discountRate; // 할인율 10 > 10%

    @Enumerated(EnumType.STRING)
    private CouponStatus status = CouponStatus.Created; //상태 [Used, Created, Deleted]

    @Enumerated(EnumType.STRING)
    private CouponType couponType;

    protected Coupon(String name, User user, int discountRate, CouponType couponType) {
        this.name = name;
        this.user = user;
        this.discountRate = discountRate;
        this.couponType = couponType;
    }


    public void changeStatusUsed() {
        this.status = CouponStatus.Used;
    }

    public String getCouponType() {
        return couponType.toString();
    }

}

