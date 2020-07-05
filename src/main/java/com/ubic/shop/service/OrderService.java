package com.ubic.shop.service;

import com.ubic.shop.domain.*;
import com.ubic.shop.repository.OrderRepository;
import com.ubic.shop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ProductService productService;

    /** 주문 */
    @Transactional
    public Long order(Long userId, Long productId, int count) {
        //엔티티 조회
        User user = userRepository.findById(userId).get();
        Product product = productService.findById(productId);

        //배송정보 생성
//        Delivery delivery = new Delivery();
//        delivery.setAddress(user.getAddress());
//        delivery.setStatus(DeliveryStatus.READY);

        //주문상품 생성
        OrderProduct orderProduct = OrderProduct.createOrderProduct(product, product.getPrice(),
                count);
        //주문 생성
        Order order = Order.createOrder(user, /*delivery, */orderProduct);
        //주문 저장
        orderRepository.save(order);
        return order.getId();
    }
    
    /** 주문 취소 */
    @Transactional
    public void cancelOrder(Long orderId) {
        //주문 엔티티 조회
        Order order = orderRepository.findOne(orderId);
        //주문 취소 -- order 만 삭제하고 order product 는 삭제 안하는데 ? -- 그래서 status 로 필터링 로직 추가했다
        order.cancel();
    }

    public List<Order> findAllOrders(Long userId) {
        return orderRepository.findAll(userId);
    }

    public List<Order> findAllOrdered(Long userId) {
        return orderRepository.findAllOrdered(userId);
    }

}
