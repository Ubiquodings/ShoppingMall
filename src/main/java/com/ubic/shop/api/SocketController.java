package com.ubic.shop.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubic.shop.elasticsearch.service.ElasticSearchService;
import com.ubic.shop.elasticsearch.service.EsSocketService;
import com.ubic.shop.service.RecommendService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Controller
@Slf4j
public class SocketController {

    private final RecommendService recommendService;
    private final ElasticSearchService elasticSearchService;
    private final ObjectMapper objectMapper;

    private final EsSocketService esSocketService; /*TODO 리팩토링!*/

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

    @MessageMapping("/hello/{nextPage}") /*해당 페이지 다음 추천 목록*/
    @SendTo("/topic/products/{userId}") /*해당 유저에게만 추천 목록 갱신*/
    public String updateProductDetailRecommendedList(@DestinationVariable(value = "0") String nextPage,
                                                     @DestinationVariable long userId,
                                                     String body){
        log.info(String.format("updateProductDetailRecommendedList : {} : {}", nextPage,userId));
        return "updateProductDetailRecommendedList";
    }

}
