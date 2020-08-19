package com.ubic.shop.domain;
import lombok.*;
//import org.springframework.data.annotation.Id;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Entity
@Getter
@Setter(AccessLevel.PROTECTED)
@Table(name = "shop_list")
public class ShopList extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "shop_list_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; //장바구니 이용 회원

//    @OneToMany(mappedBy = "shopList", cascade = CascadeType.ALL)
//    private List<ShopListProduct> shopListProducts = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
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


    //==연관관계 메서드==//
//    public void addShopListProduct(ShopListProduct shopListProduct) {
//        shopListProducts.add(shopListProduct);
//        shopListProduct.setShopList(this);
//    }

    //==생성 메서드==//
//    public static ShopList createShopList(User user, ShopListProduct... shopListProducts) {
//        ShopList shopList = new ShopList();
//        shopList.setUser(user);
//        for (ShopListProduct shopListProduct : shopListProducts) {
//            shopList.addShopListProduct(shopListProduct);
//        }
////        shopList.setStatus(ShopListStatus.ORDER);
//        return shopList;
//    }

    //==비즈니스 로직==//
    /** 장바구니 취소 */
//    public void cancel() {
////        this.setStatus(ShopListStatus.CANCEL);
//        for (ShopListProduct shopListProduct : shopListProducts) {
//            shopListProduct.cancel(); // 안에서 아무것도 하지 않는다
//        }
//    }

    //==조회 로직==//
    /** 전체 주문 가격 조회 */
//    public int getTotalPrice() {
//        int totalPrice = 0;
//        for (ShopListProduct shopListProduct : shopListProducts) {
//            totalPrice += shopListProduct.getTotalPrice();
//        }
//        return totalPrice;
//    }

}
