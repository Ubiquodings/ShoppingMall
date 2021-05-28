package com.ubic.shop.api.socket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubic.shop.config.UbicConfig;
import com.ubic.shop.domain.Product;
import com.ubic.shop.dto.CouponRequestDto;
import com.ubic.shop.dto.PublishedCouponInfoResponseDto;
import com.ubic.shop.repository.ProductRepository;
import com.ubic.shop.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
@Slf4j
public class SocketController { // 기존 소켓 테스트 코드

    private final ObjectMapper objectMapper;

    private final ProductRepository productRepository;
    private final UbicConfig ubicConfig;

    private final SimpMessagingTemplate socketTemplate;
    private final CouponService couponService;


    /**
     * [사용법]
     * String text = "[" + getTimestamp() + "]:" + greeting;
     * ObjectMapper 이용해서 json to String 변환하기!
     * this.template.convertAndSend("/topic/greetings", text);
     */


    @MessageMapping("/coupons/{userID}") /*망설이지마세요 쿠폰*/
    public void requestDoNotHesitateCoupon(CouponRequestDto requestDto, @DestinationVariable long userID
    ) throws JsonProcessingException {
        Long productId = requestDto.getProductId();
        Product product = productRepository.findById(productId).get();

        // 해당 유저에게 쿠폰 발급하기
        // 발급 전 쿠폰 있는지 확인하기
        int discountRate = 10;
        String couponName = product.getName() + " 망설이지마세요! " + discountRate + "% 쿠폰!!";
        log.info("\n사용자 ID: " + userID);
        log.info("\n쿠폰 발급합니다 : " + couponName);


        couponService.createProductCoupon(product, userID, discountRate, couponName);

        // 소켓 응답하면 화면에서 쿠폰 버튼만 바꿔주기기
        PublishedCouponInfoResponseDto couponInfo = PublishedCouponInfoResponseDto.builder()
                .couponName(couponName)
                .build();
        socketTemplate.convertAndSend("/topic/coupons/" + userID, objectMapper.writeValueAsString(couponInfo));
    }
}