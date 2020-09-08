package com.ubic.shop.domain;

import lombok.*;
import javax.persistence.*;

@NoArgsConstructor
@Entity
@Getter
@Setter(AccessLevel.PROTECTED)
@Table(name = "payment")
public class Payment extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "payment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; //장바구니 이용 회원

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product; //주문 상품
    private long count; //장바구니에 담겼던 수량

    @Builder
    public Payment(User user, Product product, long count) {
        this.user = user;
        this.product = product;
        this.count = count;
    }

    public void changeCount(Long count) {
        this.count = count;
    }

}
