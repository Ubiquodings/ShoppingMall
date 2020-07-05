package com.ubic.shop.domain;
import lombok.*;
//import org.springframework.data.annotation.Id;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Entity
@Getter
@Setter(AccessLevel.PROTECTED)
@Table(name = "orders")
public class Order extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; //주문 회원

    @Enumerated(EnumType.STRING)
    private OrderStatus status; //주문상태 [ORDER, CANCEL]

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderProduct> orderProducts = new ArrayList<>();

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
    /** 주문 취소 */
    public void cancel() { // TODO  Delivery 추가 ?
//        if (delivery.getStatus() == DeliveryStatus.COMP) {
//            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니
//                    다.");
//        }
        this.setStatus(OrderStatus.CANCEL);
        for (OrderProduct orderProduct : orderProducts) { // TODO 양방향 설정해야 할듯
            orderProduct.cancel();
        }
    }

    //==조회 로직==//
    /** 전체 주문 가격 조회 */
    public int getTotalPrice() {
        int totalPrice = 0;
        for (OrderProduct orderProduct : orderProducts) {
            totalPrice += orderProduct.getTotalPrice();
        }
        return totalPrice;
    }

//    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @JoinColumn(name = "delivery_id")
//    private Delivery delivery; //배송정보

    //==연관관계 메서드==//
//    public void setUser(User user) {
//        this.user = user;
//        user.getOrders().add(this);
//    }
//
//    public void addOrderProduct(OrderProduct orderProduct) {
//        orderProducts.add(orderProduct);
//        orderProduct.setOrder(this);
//    }
//
//    public void setDelivery(Delivery delivery) {
//        this.delivery = delivery;
//        delivery.setOrder(this);
//    }
}
