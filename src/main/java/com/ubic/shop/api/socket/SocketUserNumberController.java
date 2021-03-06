package com.ubic.shop.api.socket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubic.shop.dto.UpdateCouponUserNumberDto;
import com.ubic.shop.dto.UpdateUserNumberDto;
import com.ubic.shop.service.UserNumberBroadcastService;
import com.ubic.shop.service.UserNumberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
@Slf4j
public class SocketUserNumberController {
    private final UserNumberService userNumberService;
    private final UserNumberBroadcastService userNumberBroadcastService;
    private final SimpMessagingTemplate socketTemplate;
    private final ObjectMapper objectMapper;

    @MessageMapping("/users/{productID}") /*각 상품 함께 열람하고 있는 사용자 수*/
    public void updateProductViewUserNumber(@DestinationVariable long productID,
                                            String body) throws JsonProcessingException {

        // data 가져오기
        UpdateUserNumberDto dto = userNumberService.getProductViewUserNumber(productID);

        String result = objectMapper.writeValueAsString(dto);

        /*해당 페이지 접속 사용자 수 브로드캐스트 갱신*/
        socketTemplate.convertAndSend("/topic/users/" + productID, result);
    }

    @MessageMapping("/users/root") /*쇼핑몰에 접속한 전체 사용자 수*/
    public void updateAllViewUserNumber(String body) throws JsonProcessingException {

        // data 가져오기
        long number = userNumberService.getAllViewUserNumber();

        String result = objectMapper.writeValueAsString(new UpdateUserNumberDto(number));

        /*해당 페이지 접속 사용자 수 브로드캐스트 갱신*/
        socketTemplate.convertAndSend("/topic/users/root", result);
    }

    @MessageMapping("/users/ordered/{productId}") /*각 상품 구매한 사용자 수*/
    public void updateProductOrderUserNumber(@DestinationVariable long productId,
                                             String body) throws JsonProcessingException {

        // data 가져오기
        UpdateUserNumberDto dto = userNumberBroadcastService.getProductOrderUserNumber(productId);
        if (dto == null) {
            dto = new UpdateUserNumberDto(productId, 0L); // 없으면 0 반환해야 한다!
        }

        String result = objectMapper.writeValueAsString(dto);

        /*해당 페이지 접속 사용자 수 브로드캐스트 갱신*/
        socketTemplate.convertAndSend("/topic/users/ordered/" + productId, result);
    }

    @MessageMapping("/users/coupons/{couponType}") /*각 쿠폰 사용한 사용자 수*/
    public void updateCouponUseUserNumber(@DestinationVariable String couponType,
                                          String body) throws JsonProcessingException {

        // data 가져오기
        UpdateCouponUserNumberDto dto = userNumberBroadcastService.getCouponUseUserNumber(couponType);
        if (dto == null) {
            dto = new UpdateCouponUserNumberDto(couponType, 0L); // 없으면 0 반환해야 한다!
        }

        String result = objectMapper.writeValueAsString(dto);

        /*해당 쿠폰 사용 사용자 수 브로드캐스트 갱신*/
        socketTemplate.convertAndSend("/topic/users/coupons/" + couponType, result);
    }

}
