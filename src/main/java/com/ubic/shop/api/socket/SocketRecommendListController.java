package com.ubic.shop.api.socket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubic.shop.config.UbicConfig;
import com.ubic.shop.domain.Product;
import com.ubic.shop.dto.ProductResponseDto;
import com.ubic.shop.elasticsearch.service.ElasticSearchService;
import com.ubic.shop.elasticsearch.service.EsSocketService;
import com.ubic.shop.repository.ProductRepository;
import com.ubic.shop.service.CouponService;
import com.ubic.shop.service.RecommendService;
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
public class SocketRecommendListController {
//    private final

    private final RecommendService recommendService;
    private final ElasticSearchService elasticSearchService;
    private final ObjectMapper objectMapper;

    private final ProductRepository productRepository;
    private final UbicConfig ubicConfig;

    private final SimpMessagingTemplate socketTemplate;
//    private final CouponService couponService;


    /*[구독 2] 상품 카테고리 기반 목록*/
        /* send: /app/products/{userId}/page/{currentPage}
        , subscribe: /topic/products/{userId}
        * */
    @MessageMapping("/products/{userId}/page/{page}") /*해당 페이지 다음 추천 목록*/   // 전송
    public void updateCategoryBaseRecommendedList(@DestinationVariable String userId,
                                                   @DestinationVariable String page,
                                                   String body) throws JsonProcessingException {
        log.info("updateProductDetailRecommendedList page: " + page + ", userId: " + userId);

        // 빈도수 높은 카테고리 id 가져오기 -- django 연동
        long categoryID = recommendService.getHighestCategoryId(userId);
        log.info("categoryID: " + categoryID);

        // product repository 에서 카운트만 가져오는 쿼리 수행
        long countByCategoryId = productRepository.countByCategoryId(categoryID);
//        log.info("countByCategoryId: " + countByCategoryId);
        long pageCount = countByCategoryId % ubicConfig.productDetailPageSize; // 전체 페이지 개수
//        log.info("pageCount: " + pageCount);

        // (카테고리 기반 찾아온 상품 수) % (페이징하는 상품 수) : 클라에서 보내는 page 가 무한 +1 증가해서!
        long restOfThePage = Long.parseLong(page) % pageCount; // restOfThePage
//        log.info("pageToLong: " + pageToLong);
//        log.info("updateProductDetailRecommendedList page: " + restOfThePage + ", count: " + pageCount);

        List<Product> result = recommendService.getCategoryBaseRecommendList(Long.toString(restOfThePage), categoryID);

        // Dto 로 변환
        List<ProductResponseDto> collect = result.stream()
                .map(p -> new ProductResponseDto(p))
                .collect(Collectors.toList());

        String resultString = objectMapper.writeValueAsString(collect);
        log.info("\nupdateProductDetailRecommendedList : query-result :" + resultString);

        socketTemplate.convertAndSend("/topic/products/" + userId, resultString);
    }


    /*[구독 1] xx님을 위한 할인 상품*/
            /* send: /app/products/discount/{userId}/page/{currentPage}
            , subscribe: /topic/products/discount/{userId}
            * */
    @MessageMapping("/products/discount/{userId}/page/{page}")
    public void updateDiscountProductRecommendedList(@DestinationVariable String userId,
                                                  @DestinationVariable String page,
                                                  String body) throws JsonProcessingException {
        log.info("updateDiscountProductRecommendedList page: " + page + ", userId: " + userId);

        // 현재로써는 장고에서 받아온 product id list 기반으로 product 가져오는 방법으로 구현할듯!
        // recommendService 의 동일한 함수 이용하겠지!

        // 그런데 쿠폰 기반은 .. 자바에서 구현해도 되는 부분 ! : 일단 동작 먼저 확인하자!
        long restOfThePage = Long.parseLong(page) % ubicConfig.productDetailPageSize;
        List<Product> result = recommendService.getTestRecommendList(restOfThePage);
        if(result == null){
            log.info("\ngetTestRecommendList null");
            return;
        }
        log.info("\nproduct list size: "+result.size());

        // Dto 로 변환
        List<ProductResponseDto> collect = result.stream()
                .map(p -> new ProductResponseDto(p))
                .collect(Collectors.toList());

        String resultString = objectMapper.writeValueAsString(collect);
        log.info("\nupdateDiscountProductRecommendedList : query-result :" + resultString);

        socketTemplate.convertAndSend("/topic/products/discount/" + userId, resultString);
    }

    /*[구독 1] 내가 본 상품의 연관 상품*/
            /* send: /app/products/related/{userId}/page/{currentPage}
            , subscribe: /topic/products/related/{userId} */
    @MessageMapping("/products/related/{userId}/page/{page}")
    public void updateRelatedProductRecommendedList(@DestinationVariable String userId,
                                                     @DestinationVariable String page,
                                                     String body) throws JsonProcessingException {
        log.info("updateRelatedProductRecommendedList page: " + page + ", userId: " + userId);

        // 현재로써는 장고에서 받아온 product id list 기반으로 product 가져오는 방법으로 구현할듯!
        // recommendService 의 동일한 함수 이용하겠지!

        // 그런데 쿠폰 기반은 .. 자바에서 구현해도 되는 부분 ! : 일단 동작 먼저 확인하자!
        long restOfThePage = Long.parseLong(page) % ubicConfig.productDetailPageSize;
        List<Product> result = recommendService.getTestRecommendList(restOfThePage);
        if(result == null){
            log.info("\ngetTestRecommendList null");
            return;
        }

        // Dto 로 변환
        List<ProductResponseDto> collect = result.stream()
                .map(p -> new ProductResponseDto(p))
                .collect(Collectors.toList());

        String resultString = objectMapper.writeValueAsString(collect);
        log.info("\nupdateRelatedProductRecommendedList : query-result :" + resultString);

        socketTemplate.convertAndSend("/topic/products/related/" + userId, resultString);
    }

    /*[구독 2] xx님을 위한 추천상품*/
            /* send: /app/products/usercf/{userId}/page/{currentPage}
            , subscribe: /topic/products/usercf/{userId} */
    @MessageMapping("/products/usercf/{userId}/page/{page}")
    public void updateCouponBaseRecommendedList(@DestinationVariable String userId,
                                                     @DestinationVariable String page,
                                                     String body) throws JsonProcessingException {
        log.info("updateCouponBaseRecommendedList page: " + page + ", userId: " + userId);

        // 현재로써는 장고에서 받아온 product id list 기반으로 product 가져오는 방법으로 구현할듯!
        // recommendService 의 동일한 함수 이용하겠지!

        // 그런데 쿠폰 기반은 .. 자바에서 구현해도 되는 부분 ! : 일단 동작 먼저 확인하자!
        long restOfThePage = Long.parseLong(page) % ubicConfig.productDetailPageSize;
        List<Product> result = recommendService.getTestRecommendList(restOfThePage);
        if(result == null){
            log.info("\ngetTestRecommendList null");
            return;
        }

        // Dto 로 변환
        List<ProductResponseDto> collect = result.stream()
                .map(p -> new ProductResponseDto(p))
                .collect(Collectors.toList());

        String resultString = objectMapper.writeValueAsString(collect);
        log.info("\nupdateCouponBaseRecommendedList : query-result :" + resultString);

        socketTemplate.convertAndSend("/topic/products/usercf/" + userId, resultString);
    }


    // TODO 사용자 전체 대상
    /*[구독 3] 요즘 잘나가는 상품*/
            /* send: /app/products/freq/page/{currentPage}
            , subscribe: /topic/products/freq */
    @MessageMapping("/products/freq/page/{page}")
    public void updateFreqProductRecommendedList(/*@DestinationVariable String userId,*/
                                                @DestinationVariable String page,
                                                String body) throws JsonProcessingException {
        log.info("updateFreqProductRecommendedList page: " + page);

        // 현재로써는 장고에서 받아온 product id list 기반으로 product 가져오는 방법으로 구현할듯!
        // recommendService 의 동일한 함수 이용하겠지!

        // 그런데 쿠폰 기반은 .. 자바에서 구현해도 되는 부분 ! : 일단 동작 먼저 확인하자!
        long restOfThePage = Long.parseLong(page) % ubicConfig.productDetailPageSize;
        List<Product> result = recommendService.getTestRecommendList(restOfThePage);
        if(result == null){
            log.info("\ngetTestRecommendList null");
            return;
        }

        // Dto 로 변환
        List<ProductResponseDto> collect = result.stream()
                .map(p -> new ProductResponseDto(p))
                .collect(Collectors.toList());

        String resultString = objectMapper.writeValueAsString(collect);
        log.info("\nupdateFreqProductRecommendedList : query-result :" + resultString);

        socketTemplate.convertAndSend("/topic/products/freq", resultString);
    }

}
