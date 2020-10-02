package com.ubic.shop.domain.user_number;

import com.ubic.shop.domain.Category;
import com.ubic.shop.domain.User;
import com.ubic.shop.domain.coupon.CategoryCouponType;
import com.ubic.shop.domain.coupon.CouponType;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@NoArgsConstructor
@Entity
@Getter
@Setter(AccessLevel.PROTECTED)
public class ProductViewUserNumber {
    long productId; long userNumber;

    @Id
    @GeneratedValue
    @Column(name = "product_view_user_number_id")
    private Long id;

    @Builder
    public ProductViewUserNumber(long productId, long userNumber) {
        this.productId = productId;
        this.userNumber = userNumber;
    }

    public void changeUserNumber(long userNumber){
        this.userNumber = userNumber;
    }

}
