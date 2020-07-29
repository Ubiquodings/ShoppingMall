package com.ubic.shop.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubic.shop.kafka.dto.ClickActionRequestDto;
import com.ubic.shop.elasticsearch.ElasticSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class UserActionConsumer {

    private final ObjectMapper objectMapper;
//    private final ElasticsearchRestTemplate esTemplate; // sb version up 2.2.x 로 새로 등장 ?
    private final ElasticSearchService elasticSearchService;

    @KafkaListener(topics = {"ubic-shop-test"}, containerFactory = "defaultKafkaListenerContainerFactory")
    public void onUserAction(ConsumerRecord<String, String> consumerRecord) throws JsonProcessingException {

        log.info("\nubic-shop-test :: ConsumerRecord : {} ", consumerRecord.value()); //ok
        // value 는 String >> json ?
        ClickActionRequestDto received = objectMapper.readValue(consumerRecord.value(), ClickActionRequestDto.class);
        log.info("\nubic-shop-test :: ObjectMapper : {} ", received ); //ok

        // 사용자-카테고리 점수 계산 로직
        elasticSearchService.updateCategoryScore(received);

        // 사용자 검색 데이터 수집 TODO 컨슈머 추가하기
//        elasticSearchService.saveSearchData(received);
    }

}
