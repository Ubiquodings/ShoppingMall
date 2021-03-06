package com.ubic.shop.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor
@Entity
@Table(name = "shop_list_product")
@Getter
@Setter
public class ShopListProduct extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "shop_list_product_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product; //주문 상품

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_list_id")
    private ShopList shopList;

    private int shopListPrice; //장바구니 가격
    private int count; //장바구니 수량

    //==생성 메서드==//
    public static ShopListProduct createShopListProduct(Product product, int shopListPrice, int count) {
        ShopListProduct shopListProduct = new ShopListProduct();
        shopListProduct.setProduct(product);
        shopListProduct.setShopListPrice(shopListPrice);
        shopListProduct.setCount(count);
        return shopListProduct;
    }


    /**
     * 주문 취소
     */
    public void cancel() {
    }

}
