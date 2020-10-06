package com.ubic.shop.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubic.shop.domain.user_number.CouponUseUserNumber;
import com.ubic.shop.domain.user_number.ProductOrderUserNumber;
import com.ubic.shop.dto.UpdateCouponUserNumberDto;
import com.ubic.shop.dto.UpdateUserNumberDto;
import com.ubic.shop.repository.user_number.CouponUseUserNumberRepository;
import com.ubic.shop.repository.user_number.ProductOrderUserNumberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserNumberBroadcastService {

    private final ProductOrderUserNumberRepository productOrderUserNumberRepository;
    private final CouponUseUserNumberRepository couponUseUserNumberRepository;
    private final SimpMessagingTemplate socketTemplate;
    private final ObjectMapper objectMapper;


    @Transactional
    public void plusProductOrderUserNumber(long productId, long number) throws JsonProcessingException { // +1, -1
        List<ProductOrderUserNumber> byProductId = productOrderUserNumberRepository.findByProductId(productId);

        if (byProductId.size() != 0) { // 결과가 있다면 숫자 바꾸기
            ProductOrderUserNumber productOrderUserNumber = byProductId.get(0); // 객체 가져오기 & 연산
            productOrderUserNumber.changeUserNumber(productOrderUserNumber.getUserNumber() + number);

        } else { // 결과가 없다면: 객체 생성해서 저장
            ProductOrderUserNumber productOrderUserNumber = ProductOrderUserNumber.builder()
                    .productId(productId)
                    .userNumber(number)
                    .build();
            productOrderUserNumberRepository.save(productOrderUserNumber);
        }

        // 소켓-브로드캐스팅 결과 전달 : 이게 되나 ?
        broadcastProductOrderUserNumber(productId);
        // 의존성 순환 문제 : 클래스 분리로 문제 해결 ? oo 된다! 상품 상세에서 바로 확인 가능!
    }

    public UpdateUserNumberDto getProductOrderUserNumber(long productId) {
        List<ProductOrderUserNumber> byProductId = productOrderUserNumberRepository.findByProductId(productId);
        if (byProductId.size() != 0) { // 결과가 있다면
            ProductOrderUserNumber productOrderUserNumber = byProductId.get(0);
            return new UpdateUserNumberDto(productOrderUserNumber.getProductId(), productOrderUserNumber.getUserNumber());
        }
        return null;
    }

    private void broadcastProductOrderUserNumber(long productId) throws JsonProcessingException {
        // data 가져오기
        UpdateUserNumberDto dto = getProductOrderUserNumber(productId);
        if (dto == null){
            log.info("\nreturn null");
            dto = new UpdateUserNumberDto(productId, 0L); // 없으면 0 반환해야 한다!
        }

        String result = objectMapper.writeValueAsString(dto);
        log.info("\nupdateOrderUserNumber : " + result);

        /*해당 페이지 접속 사용자 수 브로드캐스트 갱신*/
        socketTemplate.convertAndSend("/topic/users/ordered/" + productId, result);
    }


    @Transactional
    public void plusCouponUseUserNumber(String couponType, long number) throws JsonProcessingException { // +1, -1
        List<CouponUseUserNumber> byProductId = couponUseUserNumberRepository.findByCouponType(couponType);

        if (byProductId.size() != 0) { // 결과가 있다면 숫자 바꾸기
            CouponUseUserNumber couponUseUserNumber = byProductId.get(0); // 객체 가져오기 & 연산
            couponUseUserNumber.changeUserNumber(couponUseUserNumber.getUserNumber() + number);

        } else { // 결과가 없다면: 객체 생성해서 저장
            log.info("\ncoupon type 못찾았음. 새로 생성합니다");
            CouponUseUserNumber couponUseUserNumber = CouponUseUserNumber.builder()
                    .couponType(couponType)
                    .userNumber(number)
                    .build();
            couponUseUserNumberRepository.save(couponUseUserNumber);
        }

        // 소켓-브로드캐스팅 결과 전달
        broadcastCouponUseUserNumber(couponType);
    }

    public UpdateCouponUserNumberDto getCouponUseUserNumber(String couponType) {
        List<CouponUseUserNumber> byProductId = couponUseUserNumberRepository.findByCouponType(couponType);
        if (byProductId.size() != 0) { // 결과가 있다면
            CouponUseUserNumber couponUseUserNumber = byProductId.get(0);
            log.info("\ngetCouponUseUserNumber : "+couponUseUserNumber.toString());
            return new UpdateCouponUserNumberDto(couponUseUserNumber.getCouponType(), couponUseUserNumber.getUserNumber());
        }
        return null;
    }

    private void broadcastCouponUseUserNumber(String couponType) throws JsonProcessingException {
        // data 가져오기
        UpdateCouponUserNumberDto dto = getCouponUseUserNumber(couponType);
        if (dto == null){
            log.info("\nreturn null");
            dto = new UpdateCouponUserNumberDto(couponType, 0L); // 없으면 0 반환해야 한다!
        }

        String result = objectMapper.writeValueAsString(dto);
        log.info("\nupdateCouponUserNumber : " + result);

        /*해당 페이지 접속 사용자 수 브로드캐스트 갱신*/
        socketTemplate.convertAndSend("/topic/users/coupons/" + couponType, result);

    }

}
