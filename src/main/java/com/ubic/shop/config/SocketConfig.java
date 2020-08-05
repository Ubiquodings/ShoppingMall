package com.ubic.shop.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubic.shop.elasticsearch.service.ElasticSearchService;
import com.ubic.shop.elasticsearch.service.EsSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.*;

@Configuration
@EnableWebSocketMessageBroker
@Slf4j
@RequiredArgsConstructor
public class SocketConfig implements WebSocketMessageBrokerConfigurer {

//    private final ElasticSearchService elasticSearchService;
    private final EsSocketService esSocketService;
    private final ObjectMapper objectMapper;
//    private final SimpMessagingTemplate socketTemplate;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        /*To client*/
        config.enableSimpleBroker("/topic");
        /*From client*/
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        /*클라이언트 소켓 연결*/
        registry.addEndpoint("/websocket").setAllowedOrigins("*").withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new MyChannelInterceptor(esSocketService, objectMapper/*,socketTemplate*/));
    }


    @RequiredArgsConstructor
    static class MyChannelInterceptor implements ChannelInterceptor {

        private final EsSocketService esSocketService;
        private final ObjectMapper objectMapper;
//        private final SimpMessagingTemplate socketTemplate;

        @Override
        public Message<?> preSend(Message<?> message, MessageChannel channel) {
            StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
//            long productId = -1L;
            if (StompCommand.CONNECT == accessor.getCommand()) { // websocket 연결요청
                log.info("\nsocket connected ");

            } else if (StompCommand.SUBSCRIBE == accessor.getCommand()) { // 채팅룸 구독요청

                long productId = getHeaderValue(message, "productId");
                if (productId == -1L) {
                    return message;
                }
                log.info("\nsocket subscribed : productId - " + productId);

                // 디테일 페이지 접속 인원수를 +1한다.
                esSocketService.plusUserCount(productId, 1L);

//               카테고리 점수와 같은  HashMap 형태로
//               {상품ID: 인원수} ES 에 저장
                /*TODO 접속자 수 소켓 연결하고, 스케줄러 세팅해서 디테일 페이지 바뀌도록하기*/

            } else if (StompCommand.DISCONNECT == accessor.getCommand()) { // Websocket 연결 종료

                long productId = getHeaderValue(message, "productId");
                if (productId == -1L) {
                    return message;
                }
                log.info("\nsocket terminated : productId - " + productId); // 함수가 다시 초기화되는 모양

                // TODO 디테일 페이지 접속 인원수를 -1한다.
                // 마지막으로 user 가 방문한 product id 를 알아야 한다
                esSocketService.plusUserCount(productId, -1L);
            }
            return message;
        }

        private Long getHeaderValue(Message<?> message, String keyName){
            Map header = (Map)message.getHeaders().get("nativeHeaders");
            if(header==null){
                return -1L;
            }
            log.info("\nsocket subscribed : header - " + header.toString());

            List<Long> listId;
            Long productId = -1L;
            assert header != null;
            if(header.containsKey(keyName)){
                try {
                    listId = objectMapper.readValue(header.get(keyName).toString(), new TypeReference<List<Long>>(){});
                    productId = listId.get(0); // Integer ? - Long 으로 타입변화하자 ?
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
            return productId;
        }
    }
}
