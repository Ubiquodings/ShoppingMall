package com.ubic.shop.api.socket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubic.shop.config.LoginUser;
import com.ubic.shop.config.UbicConfig;
import com.ubic.shop.domain.Product;
import com.ubic.shop.domain.Role;
import com.ubic.shop.domain.User;
import com.ubic.shop.dto.ProductResponseDto;
import com.ubic.shop.dto.SessionUser;
import com.ubic.shop.elasticsearch.service.ElasticSearchService;
import com.ubic.shop.repository.ProductRepository;
import com.ubic.shop.repository.UserRepository;
import com.ubic.shop.service.RecommendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
@Slf4j
public class SocketRecommendListController {

    private final RecommendService recommendService;
    private final ObjectMapper objectMapper;

    private final ProductRepository productRepository;
    private final UbicConfig ubicConfig;

    private final SimpMessagingTemplate socketTemplate;
    private final UserRepository userRepository;


    /*[구독 2] 상품 카테고리 기반 목록*/
        /* send: /app/products/{userId}/page/{currentPage}
        , subscribe: /topic/products/{userId}
        * */
    @MessageMapping("/products/{userId}/page/{page}") /*해당 페이지 다음 추천 목록*/   // 전송
    public void updateCategoryBaseRecommendedList(@DestinationVariable String userId,
                                                  @DestinationVariable String page,
                                                  String body) throws JsonProcessingException {

        // 빈도수 높은 카테고리 id 가져오기 -- django 연동
        long categoryID = recommendService.getHighestCategoryId(userId);

        // product repository 에서 카운트만 가져오는 쿼리 수행
        long countByCategoryId = productRepository.countByCategoryId(categoryID);
        long pageCount = countByCategoryId % ubicConfig.productDetailPageSize; // 전체 페이지 개수

        // (카테고리 기반 찾아온 상품 수) % (페이징하는 상품 수) : 클라에서 보내는 page 가 무한 +1 증가해서!
        long restOfThePage = Long.parseLong(page) % pageCount; // restOfThePage

        List<Product> result = recommendService.getCategoryBaseRecommendList(Long.toString(restOfThePage), categoryID);

        // Dto 로 변환
        List<Long> productIdList = new ArrayList<>();
        List<ProductResponseDto> collect = result.stream()
                .map(p -> {
                    productIdList.add(p.getId());
                    return new ProductResponseDto(p);
                })
                .collect(Collectors.toList());
        log.info("\n사용자 ID: " + userId + "\n상품 카테고리 기반 목록 : " + productIdList.toString());

        String resultString = objectMapper.writeValueAsString(collect);

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

        // 그런데 쿠폰 기반은 .. 자바에서 구현해도 되는 부분 ! : 일단 동작 먼저 확인하자!
        long restOfThePage = Long.parseLong(page) % ubicConfig.productDetailPageSize;
        List<Product> result = recommendService.getTestRecommendList1(restOfThePage);
        if (result == null) {
            return;
        }

        // Dto 로 변환
        List<Long> productIdList = new ArrayList<>();
        List<ProductResponseDto> collect = result.stream()
                .map(p -> {
                    productIdList.add(p.getId());
                    return new ProductResponseDto(p);
                })
                .collect(Collectors.toList());
        log.info("\n사용자 ID: " + userId + "\nxx님을 위한 할인 상품 : " + productIdList.toString());

        String resultString = objectMapper.writeValueAsString(collect);

        socketTemplate.convertAndSend("/topic/products/discount/" + userId, resultString);
    }

    /*[구독 1] 내가 본 상품의 연관 상품*/
            /* send: /app/products/related/{userId}/page/{currentPage}
            , subscribe: /topic/products/related/{userId} */
    @MessageMapping("/products/related/{userId}/page/{page}")
    public void updateRelatedProductRecommendedList(@DestinationVariable String userId,
                                                    @DestinationVariable String page,
                                                    String body) throws JsonProcessingException {

        long restOfThePage = Long.parseLong(page) % ubicConfig.productDetailPageSize;
        List<Product> result = recommendService.getTestRecommendList2(restOfThePage);
        if (result == null) {
            return;
        }

        // Dto 로 변환
        List<Long> productIdList = new ArrayList<>();
        List<ProductResponseDto> collect = result.stream()
                .map(p -> {
                    productIdList.add(p.getId());
                    return new ProductResponseDto(p);
                })
                .collect(Collectors.toList());
        log.info("\n사용자 ID: " + userId + "\n내가 본 상품의 연관 상품 : " + productIdList.toString());

        String resultString = objectMapper.writeValueAsString(collect);

        socketTemplate.convertAndSend("/topic/products/related/" + userId, resultString);
    }

    /*[구독 2] xx님을 위한 추천상품*/
            /* send: /app/products/usercf/{userId}/page/{currentPage}
            , subscribe: /topic/products/usercf/{userId} */
    @MessageMapping("/products/usercf/{userId}/page/{page}")
    public void updateCouponBaseRecommendedList(@DestinationVariable String userId,
                                                @DestinationVariable String page,
                                                String body) throws JsonProcessingException {

        long restOfThePage = Long.parseLong(page) % ubicConfig.productDetailPageSize;
        List<Product> result = recommendService.getTestRecommendList3(restOfThePage);
        if (result == null) {
            return;
        }

        // Dto 로 변환
        List<Long> productIdList = new ArrayList<>();
        List<ProductResponseDto> collect = result.stream()
                .map(p -> {
                    productIdList.add(p.getId());
                    return new ProductResponseDto(p);
                })
                .collect(Collectors.toList());
        log.info("\n사용자 ID: " + userId + "\nxx님을 위한 추천상품 : " + productIdList.toString());

        String resultString = objectMapper.writeValueAsString(collect);

        socketTemplate.convertAndSend("/topic/products/usercf/" + userId, resultString);
    }


    // TODO 사용자 전체 대상
    /*[구독 3] 요즘 잘나가는 상품*/
            /* send: /app/products/freq/page/{currentPage}
            , subscribe: /topic/products/freq */
    @MessageMapping("/products/freq/page/{page}")
    public void updateFreqProductRecommendedList(
            @DestinationVariable String page,
            String body) throws JsonProcessingException {

        long restOfThePage = Long.parseLong(page) % ubicConfig.productDetailPageSize;
        List<Product> result = recommendService.getTestRecommendList4(restOfThePage);
        if (result == null) {
            return;
        }

        // Dto 로 변환
        List<Long> productIdList = new ArrayList<>();
        List<ProductResponseDto> collect = result.stream()
                .map(p -> {
                    productIdList.add(p.getId());
                    return new ProductResponseDto(p);
                })
                .collect(Collectors.toList());
        log.info("\n요즘 잘나가는 상품 : " + productIdList.toString());

        String resultString = objectMapper.writeValueAsString(collect);

        socketTemplate.convertAndSend("/topic/products/freq", resultString);
    }

    private Long getUserId(@LoginUser SessionUser user, HttpServletRequest request) {
        Long userId;
        if (user != null) {
            userId = user.getId();
        } else { // 해시코드 다섯글자만 추출하기
            User nonMember = getTempUser(request);
            userId = nonMember.getId();
        }
        return userId;
    }

    private User getTempUser(HttpServletRequest request) {
        HttpSession session = request.getSession();
        User nonMember = null;
        if (session.isNew()) {
            log.info("\nsession is new : " + session.getId());
            // user 생성
            nonMember = User.builder()
                    .name(session.getId())
                    .email(null)
                    .picture(null)
                    .role(Role.GUEST)
                    .build();
            userRepository.save(nonMember);
        } else { // 새로운 세션이 아니라면 기존 세션이 있다는 말이니까!
            nonMember = userRepository.findByName(session.getId());
        }
        return nonMember;
    }

    /*[구독 1] 해당 상품과 이런 상품도 함께 구매했어요*/
            /* send: /app/products/buywith/{productId}/page/{currentPage} // 소켓 서버에서 userId 를 사용하진 않지만 , 서버>클라 데이터 전송하기 위해!
            , subscribe: /topic/products/buywith/{productId}
            * */
    @MessageMapping("/products/buywith/{productId}/page/{page}")
    public void updateDetailBuyTogetherRecommendedList(@DestinationVariable String productId,
                                                       @DestinationVariable String page,
                                                       String body/*, @LoginUser SessionUser user, HttpServletRequest request*/) throws JsonProcessingException {

        long restOfThePage = Long.parseLong(page) % ubicConfig.productDetailPageSize;
        List<Product> result = recommendService.getTestRecommendList5(restOfThePage);
        if (result == null) {
            return;
        }

        // Dto 로 변환
        List<Long> productIdList = new ArrayList<>();
        List<ProductResponseDto> collect = result.stream()
                .map(p -> {
                    productIdList.add(p.getId());
                    return new ProductResponseDto(p);
                })
                .collect(Collectors.toList());
        log.info("\n상품 ID: " + productId + "\n해당 상품과 이런 상품도 함께 구매했어요 : " + productIdList.toString());

        String resultString = objectMapper.writeValueAsString(collect);

        socketTemplate.convertAndSend("/topic/products/buywith/" + productId, resultString);
    }


    /*[구독 2] 목록에서 함께산 상품목록*/
                        /* send: /app/products/buywith/order-list/{userId}/page/{currentPage} // 소켓 서버에서 userId 를 사용하진 않지만 , 서버>클라 데이터 전송하기 위해!
                        , subscribe: /topic/products/buywith/order-list/{userId}
                        * */
    @MessageMapping("/products/buywith/order-list/{userId}/page/{page}")
    public void updateOrderListBuyTogetherRecommendedList(@DestinationVariable String userId,
                                                          @DestinationVariable String page,
                                                          String body) throws JsonProcessingException {

        long restOfThePage = Long.parseLong(page) % 3L;//ubicConfig.productDetailPageSize;
        List<Product> result = recommendService.getTestRecommendList6(restOfThePage);
        if (result == null) {
            return;
        }

        // Dto 로 변환
        List<Long> productIdList = new ArrayList<>();
        List<ProductResponseDto> collect = result.stream()
                .map(p -> {
                    productIdList.add(p.getId());
                    return new ProductResponseDto(p);
                })
                .collect(Collectors.toList());
        log.info("\n사용자 ID: " + userId + "\n목록에서 함께산 상품목록 : " + productIdList.toString());

        String resultString = objectMapper.writeValueAsString(collect);

        socketTemplate.convertAndSend("/topic/products/buywith/order-list/" + userId, resultString);
    }

}
