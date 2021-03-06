package com.ubic.shop.service;

import com.ubic.shop.domain.*;
import com.ubic.shop.kafka.service.KafkaSevice;
import com.ubic.shop.repository.OrderRepository;
import com.ubic.shop.repository.ShopListRepository;
import com.ubic.shop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final ShopListRepository shopListRepository;
    private final KafkaSevice kafkaService;

    /**
     * 주문
     */ // 장바구니에서 하나 주문
    // 주문은 - 장바구니에서 하나 이상 주문, - 장바구니에서 전체 주문,
    @Transactional
    public Long orderOneFromShopList(Long userId, Long productId, int count, Long shopListId) {
        //엔티티 조회
        User user = userRepository.findById(userId).get();
        Product product = productService.findById(productId);

        //주문상품 생성
        OrderProduct orderProduct = OrderProduct.createOrderProduct(product, product.getPrice(),
                count);
        //주문 생성
        Order order = Order.createOrder(user, /*delivery, */orderProduct);
        //주문 저장
        orderRepository.save(order);

        // 장바구니에서 삭제
        shopListRepository.deleteById(shopListId);

        return order.getId();
    }

    @Transactional
    public Long orderAllFromShopList(Long userId, Long productId, int count, Long shopListId) {
        //엔티티 조회
        User user = userRepository.findById(userId).get();
        Product product = productService.findById(productId);

        //주문상품 생성
        OrderProduct orderProduct = OrderProduct.createOrderProduct(product, product.getPrice(),
                count);
        //주문 생성
        Order order = Order.createOrder(user, /*delivery, */orderProduct);
        //주문 저장
        orderRepository.save(order);

        // 장바구니에서 삭제
        //shopListRepository.deleteById(shopListId);

        return order.getId();
    }

    @Transactional // - 상품 디테일에서 바로 주문:장바구니가 없구만 : 개수 하나 이상
    public Long orderOneFromDetail(Long userId, Long productId, int count) {
        //엔티티 조회
        User user = userRepository.findById(userId).get();
        Product product = productService.findById(productId);

        //주문상품 생성
        OrderProduct orderProduct = OrderProduct.createOrderProduct(product, product.getPrice(),
                count);
        //주문 생성
        Order order = Order.createOrder(user, /*delivery, */orderProduct);
        order.initTitleAndTotalPrice();

        //주문 저장
        orderRepository.save(order);

        return order.getId();
    }


    /**
     * 주문 취소
     */
    @Transactional
    public void cancelOrder(Long orderId, Long clientId) {
        //주문 엔티티 조회
        Optional<Order> orderbyId = orderRepository.findById(orderId);
        if (orderbyId.isPresent()) {
            Order order = orderbyId.get();
            order.cancel();

            for (OrderProduct orderProduct : order.getOrderProducts()) {
                String action = "order-cancel";
                Product product = orderProduct.getProduct();
                kafkaService.buildKafkaRequest(clientId, product, action);
            }
        }
    }

    public List<Order> findAllOrders(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public List<Order> findAllOrdered(Long userId) {
        return orderRepository.findByUserIdAndOrderStatus(userId, OrderStatus.ORDER);
    }

    @Transactional
    public void save(Order order) {
        orderRepository.save(order);
    }
}