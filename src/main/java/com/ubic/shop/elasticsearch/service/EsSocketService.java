package com.ubic.shop.elasticsearch.service;

import com.ubic.shop.elasticsearch.domain.ProductPageUserNumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.query.GetQuery;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class EsSocketService {

    private final ElasticsearchRestTemplate esTemplate; // sb version up 2.2.x 로 새로 등장 ?

    public long plusUserCount(long productId, long count) {
        log.info("\nplusUserCount : count : " + count);

        // 업데이트
        // 기존 es 객체 가져와야 한다
        String productID = Long.toString(productId); //String);/*received.getUserId();*/
        ProductPageUserNumber productUserNumber = esTemplate.queryForObject(
                GetQuery.getById(productID), ProductPageUserNumber.class);

        HashMap<Long, Long> map;
        long userNumber = -1L;
        if (productUserNumber == null) { // 결과가 없으면 객체 새로 생성해서 작업 진행
            productUserNumber = new ProductPageUserNumber();
            map = productUserNumber.getUserNumber();
            userNumber = count;
        } else { // 결과가 있는 상태라면
            map = productUserNumber.getUserNumber(); // 가져오기
            // 키 값이 있는지도 확인했어야 했다!
            if (map.containsKey(productId)) { // 키 있다면
                userNumber = map.get(productId) + count;
            } else { // 키 없다면
                userNumber = count;
            }
        }
        map.put(productId, userNumber);

        // 인덱스는 직접 생성했다

        // 문서 추가 -- 수행하면 ES 에 저장된 상태!
        IndexQuery indexQuery = new IndexQueryBuilder()
                .withId(productID) // _id
                .withObject(productUserNumber) // list string
                .build();
        log.info("\n product detail user number id : " + esTemplate.index(indexQuery) + "\n");

        return userNumber;
    }

    public long getProductDetailUserNumber(long productID) {
        long number = 0;
        ProductPageUserNumber numberMap = esTemplate.queryForObject(
                GetQuery.getById(Long.toString(productID)), ProductPageUserNumber.class);

        if (numberMap != null) { // es 에서 정상적으로 가져왔다면
            HashMap<Long, Long> map = numberMap.getUserNumber();
            number = map.get(productID);
        }
        return number;
    }
}
