package com.ubic.shop.domain;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Entity
@Getter
@Setter(AccessLevel.PROTECTED)
@Table(name = "orders")
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; //주문 회원

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.ORDER; //주문상태 [ORDER, CANCEL]

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderProduct> orderProducts = new ArrayList<>();

    private String title = "";
    private long totalPrice = 0;

    //==연관관계 메서드==//
    public void addOrderProduct(OrderProduct orderProduct) {
        orderProducts.add(orderProduct);
        orderProduct.setOrder(this);
    }

    //==생성 메서드==//
    public static Order createOrder(User user, OrderProduct... orderProducts) {
        Order order = new Order();
        order.setUser(user);
        for (OrderProduct orderProduct : orderProducts) {
            order.addOrderProduct(orderProduct);
        }
        order.setStatus(OrderStatus.ORDER);
        return order;
    }

    //==비즈니스 로직==//

    /**
     * 주문 취소
     */
    public void cancel() {
        this.setStatus(OrderStatus.CANCEL);
        for (OrderProduct orderProduct : orderProducts) { // TODO 양방향 설정해야 할듯
            orderProduct.cancel();
        }
    }

    /**
     * 전체 주문 가격 조회
     */
    public int getTotalPrice() {
        int totalPrice = 0;
        for (OrderProduct orderProduct : orderProducts) {
            totalPrice += orderProduct.getTotalPrice();
        }
        return totalPrice;
    }

    public void initTitleAndTotalPrice() {
        this.title = orderProducts.get(0).getProduct().getName() + " 그 외 " + (orderProducts.size() - 1) + "종류";
        this.totalPrice = getTotalPrice();
    }

}
