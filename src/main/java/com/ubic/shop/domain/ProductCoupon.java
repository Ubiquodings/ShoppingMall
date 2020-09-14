package com.ubic.shop.domain;

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

    @Builder
    public ProductCoupon(String name, User user, Product product, int discountRate) {
        super(name, user, discountRate);
        this.product = product;
    }

}
