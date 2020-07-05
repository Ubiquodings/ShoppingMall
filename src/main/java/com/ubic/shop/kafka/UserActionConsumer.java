package com.ubic.shop.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubic.shop.dto.UserActionKafkaRequestDto;
import com.ubic.shop.es_repository.UserAction;
import com.ubic.shop.service.ElasticSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
//import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.GetQuery;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

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
        UserActionKafkaRequestDto received = objectMapper.readValue(consumerRecord.value(), UserActionKafkaRequestDto.class);
        log.info("\nubic-shop-test :: ObjectMapper : {} ", received ); //ok

        elasticSearchService.updateCategoryScore(received);
    }

}
