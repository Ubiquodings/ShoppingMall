package com.ubic.shop.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubic.shop.elasticsearch.service.ElasticSearchService;
import com.ubic.shop.elasticsearch.service.EsSocketService;
import com.ubic.shop.service.UserNumberService;
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

    //    private final EsSocketService esSocketService;
    private final ObjectMapper objectMapper;
    private final UserNumberService userNumberService;


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
        registration.interceptors(new MyChannelInterceptor(objectMapper, userNumberService));
    }


    @RequiredArgsConstructor
    static class MyChannelInterceptor implements ChannelInterceptor {

        //        private final EsSocketService esSocketService;
        private final ObjectMapper objectMapper;
        private final UserNumberService userNumberService;

        @Override
        public Message<?> preSend(Message<?> message, MessageChannel channel) {
            StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

            if (StompCommand.CONNECT == accessor.getCommand()) { // websocket 연결요청


                // 전체 페이지 접속 인원수 +1
                List<Long> list = getHeaderValue(message, "userId");
                if (list.size() == 0)
                    return message;
                long userId = list.get(0);
                userNumberService.plusAllViewUserNumber(1L);

                log.info("\nsocket connected : userId - " + userId);

            } else if (StompCommand.SUBSCRIBE == accessor.getCommand()) { // 채팅룸 구독요청

//                long productId =
                List<Long> list = getHeaderValue(message, "productId");
                if (list.size() == 0)
                    return message;
                long productId = list.get(0);

                log.info("\nsocket subscribed : productId - " + productId);

                // 상품 열람 접속 인원수를 +1한다.
//                esSocketService.plusUserCount(productId, 1L);
                userNumberService.plusProductViewUserNumber(productId, 1L);

            } else if (StompCommand.DISCONNECT == accessor.getCommand()) { // Websocket 연결 종료

                // 전체 페이지 접속 인원수 -1
                List<Long> list = getHeaderValue(message, "userId");

                // 상품열람 접속 인원수를 -1한다.
                List<Long> productIdList = getHeaderValue(message, "productIdList");
                log.info("\nsocket disconnected : productIdList - " + productIdList.toString());
                for (Long productId : productIdList) {
                    userNumberService.plusProductViewUserNumber(productId, -1L);
                }

                if (list.size() == 0)
                    return message;
                long userId = list.get(0);
                log.info("\nsocket disconnected : userId - " + userId);
                userNumberService.plusAllViewUserNumber(-1L);
            }
            return message;
        }

        private List<Long> getHeaderValue(Message<?> message, String keyName) {
            Map header = (Map) message.getHeaders().get("nativeHeaders");
            if (header == null) {
                return new ArrayList<>();
            }
            log.info("\nsocket subscribed : header - keyName: " + keyName + ", " + header.toString());

            List<Long> listId;
            Long headerValue = -1L;
//            assert header != null;
            if (header.containsKey(keyName)) {
                try {
                    return objectMapper.readValue(header.get(keyName).toString(), new TypeReference<List<Long>>() {
                    });
//                    headerValue = listId.get(0); // Integer ? - Long 으로 타입변화하자 ?
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
            return new ArrayList<>();
        }
    }
}
