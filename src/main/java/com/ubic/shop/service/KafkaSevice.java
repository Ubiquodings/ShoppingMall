package com.ubic.shop.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubic.shop.dto.UserActionKafkaRequestDto;
import com.ubic.shop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class KafkaSevice {

//    ProductRepository

    private final KafkaTemplate<String,String> kafkaTemplate;
    String topic = "ubic-shop-test";
    private final ObjectMapper objectMapper;

    public ListenableFuture<SendResult<String,String>> sendToTopic(UserActionKafkaRequestDto requestDto) throws JsonProcessingException {

        // 카프카 토픽에 전송한다 -- 프로듀서 삽질 좀 해보겠지
        String key = requestDto.toString();
        String value = objectMapper.writeValueAsString(requestDto);
        ProducerRecord<String,String> producerRecord = buildProducerRecord(key, value, topic);

        ListenableFuture<SendResult<String,String>> listenableFuture =  kafkaTemplate.send(producerRecord);
        listenableFuture.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onFailure(Throwable ex) {
                handleFailure(key, value, ex);
            }

            @Override
            public void onSuccess(SendResult<String, String> result) {
                handleSuccess(key, value, result);
            }
        });
        return listenableFuture;
        // 그리고 컨슈머 작업 들어가기
    }

    private ProducerRecord<String, String> buildProducerRecord(String key, String value, String topic) {
        List<Header> recordHeaders = List.of(new RecordHeader("event-source", "scanner".getBytes()));
        return new ProducerRecord<>(topic, null, key, value, recordHeaders);
    }

    private void handleFailure(String key, String value, Throwable ex) {
        log.error("Error Sending the Message and the exception is {}", ex.getMessage());
        try {
            throw ex;
        } catch (Throwable throwable) {
            log.error("Error in OnFailure: {}", throwable.getMessage());
        }
    }

    private void handleSuccess(String key, String value, SendResult<String, String> result) {
        log.info("Message Sent SuccessFully for the key : {} and the value is {} , partition is {}", key, value, result.getRecordMetadata().partition());
    }
}
