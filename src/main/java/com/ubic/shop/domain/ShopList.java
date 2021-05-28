package com.ubic.shop.domain;

import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@Entity
@Getter
@Setter(AccessLevel.PROTECTED)
@Table(name = "shop_list")
public class ShopList extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "shop_list_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; //장바구니 이용 회원

    @OneToOne(fetch = FetchType.LAZY) // casecade all 하면 product 도 지우려 한다
    @JoinColumn(name = "product_id")
    private Product product; //주문 상품
    private long count; //장바구니 수량

    @Builder
    public ShopList(User user, Product product, long count) {
        this.user = user;
        this.product = product;
        this.count = count;
    }

    public void changeCount(Long count) {
        this.count = count;
    }

}