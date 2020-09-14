package com.ubic.shop.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubic.shop.domain.ShopList;
import com.ubic.shop.kafka.dto.ClickActionRequestDto;
import com.ubic.shop.elasticsearch.service.ElasticSearchService;
import com.ubic.shop.kafka.dto.SearchActionRequestDto;
import com.ubic.shop.repository.ShopListRepository;
import com.ubic.shop.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
@Slf4j
public class UserActionConsumer {

    private final ObjectMapper objectMapper;
    //    private final ElasticsearchRestTemplate esTemplate; // sb version up 2.2.x 로 새로 등장 ?
    private final ElasticSearchService elasticSearchService;
    private final CouponService couponService;


    @KafkaListener(topics = {"ubic-shop-test"}, containerFactory = "defaultKafkaListenerContainerFactory")
    public void onUserAction(ConsumerRecord<String, String> consumerRecord) throws JsonProcessingException {

        log.info("\nubic-shop-test :: ConsumerRecord : {} ", consumerRecord.value());
        ClickActionRequestDto received;
        try {
            received = objectMapper.readValue(consumerRecord.value(), ClickActionRequestDto.class);
        } catch (Exception e) {
            log.info("\n카프카 컨슈머 실패 :" + e.getMessage());
            return;
        }
        log.info("\nkafka consumer user action: " + received.toString());

        // 장바구니 쿠폰 심사
        log.info("\nonUserAction : actionType"+received.getActionType());
        if (received.getActionType().equals("cart")) {
            couponService.checkCartCategoryCoupon(received);
        }

        // 사용자 행동 es 에 저장하는 로직
        elasticSearchService.updateCategoryScore(received);

    }


    @KafkaListener(topics = {"ubic-shop-search"}, containerFactory = "defaultKafkaListenerContainerFactory")
    public void onUserSearchAction(ConsumerRecord<String, String> consumerRecord) throws JsonProcessingException {

        log.info("\nubic-shop-search :: ConsumerRecord : {} ", consumerRecord.value());
        SearchActionRequestDto received = objectMapper.readValue(consumerRecord.value(), SearchActionRequestDto.class);

        // TODO 검색 이력 ES 에 저장
        // 아 이걸 못한 이유가 index 꽉 차서였어...! 아니다 원래 있었음 아무튼 정리함!
        elasticSearchService.saveSearchData(received);

        // 사용자-카테고리 점수 계산 로직
//        elasticSearchService.updateCategoryScore(received);
    }


}
