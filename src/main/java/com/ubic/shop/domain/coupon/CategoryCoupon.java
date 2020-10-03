package com.ubic.shop.domain.coupon;

import com.ubic.shop.domain.Category;
import com.ubic.shop.domain.User;
import lombok.*;

import javax.persistence.*;

@Entity
@DiscriminatorValue("category")
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor
public class CategoryCoupon extends Coupon {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Enumerated(EnumType.STRING)
    private CategoryCouponType categoryCouponType;

//    @Enumerated(EnumType.STRING)
//    private CouponType couponType = CouponType.category_base;


    @Builder
    public CategoryCoupon(String name, User user, Category category, int discountRate, CategoryCouponType categoryCouponType) {
        super(name, user, discountRate, CouponType.category_base);
        this.category = category;
        this.categoryCouponType = categoryCouponType;
    }

}