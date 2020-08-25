package com.ubic.shop.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubic.shop.config.UbicConfig;
import com.ubic.shop.domain.Coupon;
import com.ubic.shop.domain.Product;
import com.ubic.shop.domain.User;
import com.ubic.shop.dto.CouponRequestDto;
import com.ubic.shop.dto.ProductResponseDto;
import com.ubic.shop.dto.SimpleMessageDto;
import com.ubic.shop.elasticsearch.service.ElasticSearchService;
import com.ubic.shop.elasticsearch.service.EsSocketService;
import com.ubic.shop.repository.CouponRepository;
import com.ubic.shop.repository.ProductRepository;
import com.ubic.shop.repository.UserRepository;
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
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
@Slf4j
public class SocketController {

    private final RecommendService recommendService;
    private final ElasticSearchService elasticSearchService;
    private final ObjectMapper objectMapper;

    private final EsSocketService esSocketService; /*TODO 리팩토링!*/

    private final ProductRepository productRepository;
    private final UbicConfig ubicConfig;

    private final SimpMessagingTemplate socketTemplate;

    /**
     * [사용법]
     * String text = "[" + getTimestamp() + "]:" + greeting;
     * ObjectMapper 이용해서 json to String 변환하기!
     * this.template.convertAndSend("/topic/greetings", text);
     */


    @MessageMapping("/users/{productID}") /*해당 페이지 접속 사용자 수*/
//    @SendTo("/topic/users/{productPK}") /*해당 페이지 접속 사용자 수 브로드캐스트 갱신*/
    public void updateUserNumber(@DestinationVariable long productID,
            /*@DestinationVariable long productPK,*/
                                 String body) throws JsonProcessingException {

        // ES 에서 가져오기
        long number = esSocketService.getProductDetailUserNumber(productID);

        String result = objectMapper.writeValueAsString(new UpdateUserNumberDto(number));
        log.info("\nupdateUserNumber : " + result);

        /*해당 페이지 접속 사용자 수 브로드캐스트 갱신*/
        socketTemplate.convertAndSend("/topic/users/" + productID, result);
    }

    @AllArgsConstructor
    @Getter
    static class UpdateUserNumberDto {
        long number;
    }

    /*왜인지 url 파람은 받지 못한다!*/
    @MessageMapping("/products/{userId}/page/{page}") /*해당 페이지 다음 추천 목록*/   // 전송
//    @SendTo("/topic/products/{userId}") /*해당 유저에게만 추천 목록 갱신*/ // 구독
    public void updateProductDetailRecommendedList(@DestinationVariable String userId,
                                                   @DestinationVariable String page,
                                                   String body) throws JsonProcessingException {
        log.info("updateProductDetailRecommendedList page: " + page + ", userId: " + userId);

        // page 받고 repo 의 count 로 나머지 연산해야 한다 : page % pageCount
        // product repository 에서 카운트만 가져오는 쿼리 수행
        long categoryID = recommendService.getHighestCategoryId(userId);
        log.info("categoryID: " + categoryID);
//        long count = productRepository.countByCategoryId(categoryID) % ubicConfig.productDetailPageSize;
        // (찾아온 상품 수) % (페이징하는 상품 수) = 6 % 8 = 6

        PageRequest pageRequest = PageRequest.of(1, ubicConfig.productDetailPageSize, Sort.by(Sort.Direction.DESC, "name"));
        Page<Product> productPageFindByCategoryId = productRepository.findByCategoryId(categoryID, pageRequest);
        if (productPageFindByCategoryId.hasContent()) {
            log.info("has content");
        } else {
            log.info("has not content");
            return;
        }
        long pageCount = productPageFindByCategoryId.getTotalPages(); // 해당 카테고리 전체 페이지 수
        log.info("pageCount: " + pageCount);

        long pageToLong = Long.parseLong(page);
        log.info("pageToLong: " + pageToLong);
        pageToLong %= pageCount;

        log.info("updateProductDetailRecommendedList page: " + pageToLong + ", count: " + pageCount);

        List<Product> result = recommendService.getRecommendList(userId, Long.toString(pageToLong)); // Dto 로 변환해야 하는데!
        List<ProductResponseDto> collect = result.stream()
                .map(p -> new ProductResponseDto(p))
                .collect(Collectors.toList());

        log.info("\nupdateProductDetailRecommendedList : query-result :" + objectMapper.writeValueAsString(collect));

        socketTemplate.convertAndSend("/topic/products/" + userId, objectMapper.writeValueAsString(collect));
//        return "updateProductDetailRecommendedList";
    }

    private final UserRepository userRepository;
    private final CouponRepository couponRepository;

    @MessageMapping("/coupons/{userID}") /*해당 페이지 접속 사용자 수*/
//    @SendTo("/topic/users/{productPK}") /*해당 페이지 접속 사용자 수 브로드캐스트 갱신*/
    public void requestDoNotHesitateCoupon(CouponRequestDto requestDto, @DestinationVariable long userID
    ) throws JsonProcessingException {
        log.info("body: " + requestDto.getProductId()); // 왜가져온거지 ?? 쿠폰 이름에 사용하려고!
        Long productId = requestDto.getProductId();
        Product product = productRepository.findById(productId).get();

        // user 정보 가져오기
        User user = userRepository.findById(userID).get();

        // 해당 유저에게 쿠폰 발급하기
        Coupon coupon = Coupon.builder()
                .name(product.getName()+" 망설이지마세요!")
                .user(user)
                .product(product)
                .build();
        couponRepository.save(coupon);

        // 소켓 응답하면 화면에서 쿠폰 버튼만 바꿔주기기
        SimpleMessageDto responseDto = new SimpleMessageDto("ok");
        socketTemplate.convertAndSend("/topic/coupons/" + user.getId(), objectMapper.writeValueAsString(responseDto));

        // 여기까지 하고 브라우저 테스트!
    }


}
