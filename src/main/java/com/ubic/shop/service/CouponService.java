package com.ubic.shop.service;

import com.ubic.shop.domain.*;
import com.ubic.shop.domain.coupon.CategoryCoupon;
import com.ubic.shop.domain.coupon.CategoryCouponType;
import com.ubic.shop.domain.coupon.Coupon;
import com.ubic.shop.domain.coupon.ProductCoupon;
import com.ubic.shop.kafka.dto.ClickActionRequestDto;
import com.ubic.shop.repository.CategoryRepository;
import com.ubic.shop.repository.CouponRepository;
import com.ubic.shop.repository.ShopListRepository;
import com.ubic.shop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CouponService {

    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final CategoryRepository categoryRepository;
    private final ShopListRepository shopListRepository;
    private final EntityManager em;
//    private final CouponService couponService;


    @Transactional
    public void checkCartCategoryCoupon(ClickActionRequestDto received) {
        log.info("\ncheckCartCategoryCoupon");

        // 장바구니 데이터분석 요청 : 스부 내에서 처리 가능
//        String actionType = received.getActionType();

        // DataAnalysisCartResponseDto :  categoryIdList
//            스부에서 해당 user 의 장바구니 데이터 모두 가져오기
        List<ShopList> shopListAllByUserId = shopListRepository.findAllByUserId(received.getUserId());
        log.info("\nshopListAllByUserId size: " + shopListAllByUserId.size());
//            장바구니 상품 루프돌면서 카테고리:카운팅 자료구조 이용
        Map<Long, Integer> categoryCounting = new HashMap<>();
        long keyCategoryId = -1L;
        for (ShopList shopList : shopListAllByUserId) {
            // 맵에 category id 있으면 증가
            // 없으면 1초 초기화
            keyCategoryId = shopList.getProduct().getCategory().getId();
            if (categoryCounting.containsKey(keyCategoryId)) { // 키 있다면
                categoryCounting.put(keyCategoryId, categoryCounting.get(keyCategoryId) + 1); // 기존값에 추가
            } else { // 키 없다면
                categoryCounting.put(keyCategoryId, 1); // 새 값 추가
            }
        }
        log.info("\ncategoryCounting size: "+categoryCounting.size());

//            자료구조 루프돌면서 3이상인 id 리턴
        List<Long> categoryIdListForCoupon = new ArrayList<>();
        categoryCounting.forEach((key, value) -> {
            if (value >= 3) {
                log.info("\ncategoryIdListForCoupon.add(key) : " + key+" value: "+value);
                categoryIdListForCoupon.add(key);
            }
        });
        publishCategoryCouponsTypeCart(categoryIdListForCoupon, received.getUserId());

    }


    // category id List 받고 중복되는 쿠폰 없다면 쿠폰 발급
    // user id 도 있어야겠지
    @Transactional
    public void publishCategoryCouponsTypeCart(List<Long> categoryIdList, Long userId) {

        int discountRate = 20;

        for (Long categoryId : categoryIdList) {
            log.info("\npublishCategoryCouponsTypeCart 쿠폰 발급 심사합니다 : "+categoryId);
            createCategoryCoupon(categoryId, userId, discountRate, CategoryCouponType.cart);
        }
    }

    @Transactional
    public long createCategoryCoupon(long categoryId, long userId, int discountRate, /*String couponName,*/
                                     CategoryCouponType categoryCouponType) {

        Category category = categoryRepository.findById(categoryId).get();
        String couponName = "장바구니에 담아두신 " + category.getName() + " 쿠폰드려요! + 20% 쿠폰!!";

        // 쿠폰 이미 있는지 확인
        log.info("\n쿠폰 발급 심사 userId : "+userId);
        List<Coupon> byCategoryAndUserAndCouponType = couponRepository
                .findByCategoryAndUserAndCategoryCouponType(categoryId, userId, categoryCouponType);

        log.info("\nbyCategoryAndUserAndCouponType : "+byCategoryAndUserAndCouponType.size());
        if (byCategoryAndUserAndCouponType.size() != 0) { // 이미 있다면
            log.info("\n이미 갖고있는 쿠폰: " + couponName);
            return -1L;
        }

        // 쿠폰 생성
        User user = userRepository.findById(userId).get();

        Coupon coupon = CategoryCoupon.builder()
                .name(couponName)
                .user(user)
                .discountRate(discountRate)
                .category(category)
                .categoryCouponType(categoryCouponType)
                .build();
        log.info("\n쿠폰 발급 userId : "+user.getId());

        coupon = couponRepository.save(coupon);
        em.flush();
        em.clear();
        return coupon.getId();

    }

    @Transactional
    public long createProductCoupon(Product product, long userId, int discountRate, String couponName) {

        User user = userRepository.findById(userId).get();

        // 쿠폰 이미 있는지 확인
        List<Coupon> CouponsByProductAndUser = couponRepository.findByProductAndUser(product.getId(), userId);
        if (CouponsByProductAndUser.size() != 0) { // 이미 있다면
            log.info("\n이미 갖고있는 쿠폰: " + couponName);
            return -1L;
        }

        // 쿠폰 생성
        Coupon coupon = ProductCoupon.builder()
                .name(couponName)
                .user(user)
                .discountRate(discountRate)
                .product(product)
                .build();

        coupon = couponRepository.save(coupon);
        return coupon.getId();
    }

    @Transactional
    public void saveChangedCoupon(Coupon coupon) {
        log.info("coupon status changed: "+coupon.toString());
        couponRepository.save(coupon);
    }


//    @Transactional
//    public void cancelOrder(Long orderId) {
//        //주문 엔티티 조회
//        if(orderRepository.findById(orderId).isPresent()){
//            Order order = orderRepository.findById(orderId).get();
//            //주문 취소 -- order 만 삭제하고 order product 는 삭제 안하는데 ? -- 그래서 status 로 필터링 로직 추가했다
//            order.cancel();
//
//        }
//    }

}
