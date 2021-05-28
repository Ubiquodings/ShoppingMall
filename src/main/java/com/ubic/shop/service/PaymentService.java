package com.ubic.shop.service;

import com.ubic.shop.domain.Payment;
import com.ubic.shop.domain.Product;
import com.ubic.shop.domain.User;
import com.ubic.shop.repository.PaymentRepository;
import com.ubic.shop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PaymentService {

    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final ProductService productService;

    @Transactional
    public Long payment(Long userId, Long productId, long count) {
        //엔티티 조회
        User user = userRepository.findById(userId).get();
        Product product = productService.findById(productId);

        // user + product 조합으로 이미 Payment 객체가 있다면 생성하지 않는다 !
        List<Payment> byUserIdAndProductId = paymentRepository.findByUserIdAndProductId(user.getId(), product.getId(), count);

        if (byUserIdAndProductId.size() != 0) { // 있다면
            return -1L;
        }

        //결제 상품 생성
        Payment payment = Payment.builder()
                .user(user)
                .product(product)
                .count(count)
                .build();

        //결제 상품 저장
        paymentRepository.save(payment);

        return payment.getId();
    }

    public List<Payment> findAllPayments(Long userId) {
        return paymentRepository.findAllByUserId(userId);
    }
}
