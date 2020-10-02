package com.ubic.shop.domain.coupon;

import com.ubic.shop.domain.Product;
import com.ubic.shop.domain.User;
import com.ubic.shop.domain.coupon.Coupon;
import lombok.*;

import javax.persistence.*;

@Entity
@DiscriminatorValue("product") // type 대체할 수 있는지 db 생성되는거 살펴보기
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor
public class ProductCoupon extends Coupon {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

//    @Enumerated(EnumType.STRING)
//    private CouponType couponType = CouponType.product_base;


    @Builder
    public ProductCoupon(String name, User user, Product product, int discountRate) {
        super(name, user, discountRate, CouponType.product_base);
        this.product = product;
//        this.couponType = Cou
    }

}
