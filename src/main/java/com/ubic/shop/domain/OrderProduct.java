package com.ubic.shop.domain;
import lombok.*;
//import org.springframework.data.annotation.Id;

import javax.persistence.*;
@NoArgsConstructor
@Entity
@Table(name = "order_product")
@Getter @Setter
public class OrderProduct extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "order_product_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product; //주문 상품

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order; //주문

    private int orderPrice; //주문 가격
    private int count; //주문 수량

    //==생성 메서드==//
    public static OrderProduct createOrderProduct(Product product, int orderPrice, int count) {
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setProduct(product); //
        orderProduct.setOrderPrice(orderPrice);
        orderProduct.setCount(count);
        product.removeStock(count);
        return orderProduct;
    }
    //==비즈니스 로직==//
    /** 주문 취소 */
    public void cancel() {
        getProduct().addStock(count);
    }
    //==조회 로직==//
    /** 주문상품 전체 가격 조회 */
    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }
}
