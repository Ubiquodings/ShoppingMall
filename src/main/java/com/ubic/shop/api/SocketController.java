package com.ubic.shop.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubic.shop.config.UbicConfig;
import com.ubic.shop.domain.Product;
import com.ubic.shop.dto.ProductResponseDto;
import com.ubic.shop.elasticsearch.service.ElasticSearchService;
import com.ubic.shop.elasticsearch.service.EsSocketService;
import com.ubic.shop.repository.ProductRepository;
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
    /**      [사용법]
     *           String text = "[" + getTimestamp() + "]:" + greeting;
     *           ObjectMapper 이용해서 json to String 변환하기!
     *           this.template.convertAndSend("/topic/greetings", text);
     * */


    @MessageMapping("/users/{productID}") /*해당 페이지 접속 사용자 수*/
//    @SendTo("/topic/users/{productPK}") /*해당 페이지 접속 사용자 수 브로드캐스트 갱신*/
    public void updateUserNumber(@DestinationVariable long productID,
                                   /*@DestinationVariable long productPK,*/
                                   String body) throws JsonProcessingException {

        // ES 에서 가져오기
        long number = esSocketService.getProductDetailUserNumber(productID);

        String result = objectMapper.writeValueAsString(new UpdateUserNumberDto(number));
        log.info("\nupdateUserNumber : "+result);

        // send 하면서 수를 늘려야하는걸수도 있어! no check@ ok
        socketTemplate.convertAndSend("/topic/users/"+productID, result);
    }

    @AllArgsConstructor @Getter
    static class UpdateUserNumberDto {
        long number;
    }

    /*왜인지 url 파람은 받지 못한다!*/
    @MessageMapping("/products/{userId}/page/{page}") /*해당 페이지 다음 추천 목록*/   // 전송
//    @SendTo("/topic/products/{userId}") /*해당 유저에게만 추천 목록 갱신*/ // 구독
    public void updateProductDetailRecommendedList(/*@DestinationVariable(value = "0") String nextPage,*/
                                                     @DestinationVariable String userId,
                                                     @DestinationVariable String page,
                                                     String body) throws JsonProcessingException {
        log.info("updateProductDetailRecommendedList page: "+page+", userId: "+userId);

        // page 받고 repo 의 count 로 나머지 연산해야 한다 : page % pageCount
        // product repository 에서 카운트만 가져오는 쿼리 수행
        long categoryID = recommendService.getHighestCategoryId(userId);
//        long count = productRepository.countByCategoryId(categoryID) % ubicConfig.productDetailPageSize;
        // (찾아온 상품 수) % (페이징하는 상품 수) = 6 % 8 = 6
        
        PageRequest pageRequest = PageRequest.of(/*0*/Integer.parseInt(page), ubicConfig.productDetailPageSize, Sort.by(Sort.Direction.DESC, "name"));
        Page<Product> productPageFindByCategoryId = productRepository.findByCategoryId(categoryID, pageRequest);
        long pageCount = productPageFindByCategoryId.getTotalPages(); // 해당 카테고리 전체 페이지 수

        long pageToLong = Long.parseLong(page);
        pageToLong = pageToLong % pageCount;

        log.info("updateProductDetailRecommendedList page: "+pageToLong+", count: "+pageCount);

        List<Product> result = recommendService.getRecommendList(userId, Long.toString(pageToLong)); // Dto 로 변환해야 하는데!
        List<ProductResponseDto> collect = result.stream()
                .map(p -> new ProductResponseDto(p))
                .collect(Collectors.toList());

        log.info("\nupdateProductDetailRecommendedList : query-result :"+objectMapper.writeValueAsString(collect));

        socketTemplate.convertAndSend("/topic/products/"+userId, objectMapper.writeValueAsString(collect));
//        return "updateProductDetailRecommendedList";
    }

}