package com.ubic.shop.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubic.shop.service.UserNumberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
@Slf4j
@RequiredArgsConstructor
public class SocketConfig implements WebSocketMessageBrokerConfigurer {

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

            } else if (StompCommand.SUBSCRIBE == accessor.getCommand()) { // 채팅룸 구독요청

                List<Long> list = getHeaderValue(message, "productId");
                if (list.size() == 0)
                    return message;
                long productId = list.get(0);

                // 상품 열람 접속 인원수를 +1한다.
                userNumberService.plusProductViewUserNumber(productId, 1L);

            } else if (StompCommand.DISCONNECT == accessor.getCommand()) { // Websocket 연결 종료

                // 전체 페이지 접속 인원수 -1
                List<Long> list = getHeaderValue(message, "userId");

                // 상품열람 접속 인원수를 -1한다.
                List<Long> productIdList = getHeaderValue(message, "productIdList");
                for (Long productId : productIdList) {
                    userNumberService.plusProductViewUserNumber(productId, -1L);
                }

                if (list.size() == 0)
                    return message;
                long userId = list.get(0);
                userNumberService.plusAllViewUserNumber(-1L);
            }
            return message;
        }

        private List<Long> getHeaderValue(Message<?> message, String keyName) {
            Map header = (Map) message.getHeaders().get("nativeHeaders");
            if (header == null) {
                return new ArrayList<>();
            }

            Long headerValue = -1L;
            if (header.containsKey(keyName)) {
                try {
                    return objectMapper.readValue(header.get(keyName).toString(), new TypeReference<List<Long>>() {
                    });
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
            return new ArrayList<>();
        }
    }
}
