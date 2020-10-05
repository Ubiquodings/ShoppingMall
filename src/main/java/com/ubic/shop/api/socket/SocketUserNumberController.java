package com.ubic.shop.api.socket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubic.shop.dto.UpdateCouponUserNumberDto;
import com.ubic.shop.dto.UpdateUserNumberDto;
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
    //    private final
    private final UserNumberService userNumberService;
    private final SimpMessagingTemplate socketTemplate;
    private final ObjectMapper objectMapper;

    @MessageMapping("/users/{productID}") /*각 상품 함께 열람하고 있는 사용자 수*/
    public void updateProductViewUserNumber(@DestinationVariable long productID,
                                            String body) throws JsonProcessingException {
        log.info("\niam socket");

        // data 가져오기
        UpdateUserNumberDto dto = userNumberService.getProductViewUserNumber(productID);
        if (dto == null){ // 없어도 된다! 예외 처리 안에서 했음
            log.info("\nreturn null");
            dto = new UpdateUserNumberDto(productID, 0L);
        }

        String result = objectMapper.writeValueAsString(dto);
        log.info("\nupdateUserNumber : " + result);

        /*해당 페이지 접속 사용자 수 브로드캐스트 갱신*/
        socketTemplate.convertAndSend("/topic/users/" + productID, result);
    }

    @MessageMapping("/users/root") /*쇼핑몰에 접속한 전체 사용자 수*/
    public void updateAllViewUserNumber(@DestinationVariable long productID,
                                        String body) throws JsonProcessingException {

        // data 가져오기
        long number = userNumberService.getAllViewUserNumber();

        String result = objectMapper.writeValueAsString(new UpdateUserNumberDto(number));
        log.info("\nupdateUserNumber : " + result);

        /*해당 페이지 접속 사용자 수 브로드캐스트 갱신*/
        socketTemplate.convertAndSend("/topic/users/root", result);
    }

    @MessageMapping("/users/ordered/{productId}") /*각 상품 구매한 사용자 수*/
    public void updateProductOrderUserNumber(@DestinationVariable long productId,
                                             String body) throws JsonProcessingException {

        // data 가져오기
        UpdateUserNumberDto dto = userNumberService.getProductOrderUserNumber(productId);
        if (dto == null)
            return;

        String result = objectMapper.writeValueAsString(dto);
        log.info("\nupdateUserNumber : " + result);

        /*해당 페이지 접속 사용자 수 브로드캐스트 갱신*/
        socketTemplate.convertAndSend("/topic/users/ordered/" + productId, result);
    }

    @MessageMapping("/users/coupons/{couponType}") /*각 쿠폰 사용한 사용자 수*/
    public void updateCouponUseUserNumber(@DestinationVariable String couponType,
                                          String body) throws JsonProcessingException {

        // data 가져오기
        UpdateCouponUserNumberDto dto = userNumberService.getCouponUseUserNumber(couponType);
        if (dto == null)
            return;

        String result = objectMapper.writeValueAsString(dto);
        log.info("\nupdateUserNumber : " + result);

        /*해당 페이지 접속 사용자 수 브로드캐스트 갱신*/
        socketTemplate.convertAndSend("/topic/users/coupons/" + couponType, result);
    }

}
