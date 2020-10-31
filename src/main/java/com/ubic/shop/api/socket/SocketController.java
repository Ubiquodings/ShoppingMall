package com.ubic.shop.api.socket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubic.shop.config.UbicConfig;
import com.ubic.shop.domain.Product;
import com.ubic.shop.dto.*;
import com.ubic.shop.elasticsearch.service.ElasticSearchService;
import com.ubic.shop.elasticsearch.service.EsSocketService;
import com.ubic.shop.repository.ProductRepository;
import com.ubic.shop.service.CouponService;
import com.ubic.shop.service.RecommendService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
@Slf4j
public class SocketController { // 기존 소켓 테스트 코드

    private final RecommendService recommendService;
    private final ElasticSearchService elasticSearchService;
    private final ObjectMapper objectMapper;

    private final EsSocketService esSocketService; /*TODO 리팩토링!*/

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

//    @MessageMapping("/users/{productID}") /*해당 페이지 접속 사용자 수*/
////    @SendTo("/topic/users/{productPK}") /*해당 페이지 접속 사용자 수 브로드캐스트 갱신*/
//    public void updateUserNumber(@DestinationVariable long productID,
//            /*@DestinationVariable long productPK,*/
//                                 String body) throws JsonProcessingException {
//
//        // ES 에서 가져오기
//        long number = esSocketService.getProductDetailUserNumber(productID);
//
//        String result = objectMapper.writeValueAsString(new UpdateUserNumberDto(number));
//        log.info("\nupdateUserNumber : " + result);
//
//        /*해당 페이지 접속 사용자 수 브로드캐스트 갱신*/
//        socketTemplate.convertAndSend("/topic/users/" + productID, result);
//    }

//    @AllArgsConstructor
//    @Getter
//    static class UpdateUserNumberDto {
//        long number;
//    }

    /*왜인지 url 파람은 받지 못한다!*/
//    @MessageMapping("/products/{userId}/page/{page}") /*해당 페이지 다음 추천 목록*/   // 전송
////    @SendTo("/topic/products/{userId}") /*해당 유저에게만 추천 목록 갱신*/ // 구독
//    public void updateProductDetailRecommendedList(@DestinationVariable String userId,
//                                                   @DestinationVariable String page,
//                                                   String body) throws JsonProcessingException {
//        log.info("updateProductDetailRecommendedList page: " + page + ", userId: " + userId);
//
//        // page 받고 repo 의 count 로 나머지 연산해야 한다 : page % pageCount
//        // product repository 에서 카운트만 가져오는 쿼리 수행
//        long categoryID = recommendService.getHighestCategoryId(userId);
//
//        log.info("categoryID: " + categoryID);
//
////        long count = productRepository.countByCategoryId(categoryID) % ubicConfig.productDetailPageSize;
//        // (찾아온 상품 수) % (페이징하는 상품 수) = 6 % 8 = 6
//
//
//        PageRequest pageRequest = PageRequest.of(1, ubicConfig.productDetailPageSize, Sort.by(Sort.Direction.DESC, "name"));
//        Page<Product> productPageFindByCategoryId = productRepository.findByCategoryId(categoryID, pageRequest);
////        productPageFindByCategoryId.stream()
////                .forEach((p) -> {
////                    p.getCategory();
////                });
//        if (productPageFindByCategoryId.hasContent()) {
//            log.info("has content");
//        } else {
//            log.info("has not content");
//            return;
//        }
//        long pageCount = productPageFindByCategoryId.getTotalPages(); // 해당 카테고리 전체 페이지 수
//        log.info("pageCount: " + pageCount);
//
//        long pageToLong = Long.parseLong(page);
//        log.info("pageToLong: " + pageToLong);
//        pageToLong %= pageCount;
//
//        log.info("updateProductDetailRecommendedList page: " + pageToLong + ", count: " + pageCount);
//
//        List<Product> result = recommendService.getRecommendList(userId, Long.toString(pageToLong)); // Dto 로 변환해야 하는데!
//        List<ProductResponseDto> collect = result.stream()
//                .map(p -> new ProductResponseDto(p))
//                .collect(Collectors.toList());
//
//        log.info("\nupdateProductDetailRecommendedList : query-result :" + objectMapper.writeValueAsString(collect));
//
//        socketTemplate.convertAndSend("/topic/products/" + userId, objectMapper.writeValueAsString(collect));
////        return "updateProductDetailRecommendedList";
//    }


    @MessageMapping("/coupons/{userID}") /*망설이지마세요 쿠폰*/
//    @SendTo("/topic/users/{productPK}")
    public void requestDoNotHesitateCoupon(CouponRequestDto requestDto, @DestinationVariable long userID
    ) throws JsonProcessingException {
//        log.info("body: " + requestDto.getProductId()); // 왜가져온거지 ?? 쿠폰 이름에 사용하려고!
        Long productId = requestDto.getProductId();
        Product product = productRepository.findById(productId).get();

        // 해당 유저에게 쿠폰 발급하기
        // 발급 전 쿠폰 있는지 확인하기
        int discountRate = 10;
        String couponName = product.getName() + " 망설이지마세요! "+discountRate+"% 쿠폰!!";
        log.info("\n사용자 ID: " + userID);
        log.info("\n쿠폰 발급합니다 : " + couponName);


        couponService.createProductCoupon(product, userID, discountRate, couponName);


        //

        // 소켓 응답하면 화면에서 쿠폰 버튼만 바꿔주기기
//        SimpleMessageDto responseDto = new SimpleMessageDto("ok");
        PublishedCouponInfoResponseDto couponInfo = PublishedCouponInfoResponseDto.builder()
                .couponName(couponName)
                .build();
        socketTemplate.convertAndSend("/topic/coupons/" + userID, objectMapper.writeValueAsString(couponInfo));

        // 여기까지 하고 브라우저 테스트!
    }



}