package com.ubic.shop.domain;


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

    protected Coupon(String name, User user, int discountRate) {
        this.name = name;
        this.user = user;
        this.discountRate = discountRate;
    }


//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "product_id")
//    private Product product;


//    @Builder
//    public Coupon(String name, User user/*, Product product*/) {
//        this.name = name;
//        this.user = user;
////        this.product = product;
//    }

    public void changeStatusUsed() {
        this.status = CouponStatus.Used;
    }


//    public void changeStatus(CouponStatus used) {
//        this.status =
//    }
}

