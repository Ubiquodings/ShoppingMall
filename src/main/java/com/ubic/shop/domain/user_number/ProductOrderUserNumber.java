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
public class ProductOrderUserNumber {
    long productId; long userNumber;

    @Id
    @GeneratedValue
    @Column(name = "product_order_user_number_id")
    private Long id;

    @Builder
    public ProductOrderUserNumber(long productId, long userNumber) {
        this.productId = productId;
        this.userNumber = userNumber;
    }

    public void changeUserNumber(long userNumber){
        this.userNumber = userNumber;
    }

}
