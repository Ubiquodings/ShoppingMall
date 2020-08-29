package com.ubic.shop.elasticsearch.service;

import com.ubic.shop.domain.Product;
import com.ubic.shop.elasticsearch.domain.ClickProductAction;
import com.ubic.shop.elasticsearch.domain.ProductPageUserNumber;
import com.ubic.shop.kafka.dto.ClickActionRequestDto;
import com.ubic.shop.elasticsearch.domain.SearchText;
import com.ubic.shop.elasticsearch.domain.CategoryScore;
import com.ubic.shop.kafka.dto.SearchActionRequestDto;
import com.ubic.shop.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.query.GetQuery;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ElasticSearchService {

    private final ElasticsearchRestTemplate esTemplate; // sb version up 2.2.x 로 새로 등장 ?
    private final ProductService productService;

    public void updateCategoryScore(ClickActionRequestDto received) {
        String actionType = received.getActionType();

        long score = 0;
        switch (actionType){
            case "click":
                score = 1;
                break;
            case "cart":
                score = 3;
                break;
            case "order":
                score = 5;
                break;
        }

        // 기존 es 객체 가져와야 한다
        String id = received.getUserId();
        CategoryScore actionScore = getESUserActionById(id);

        Product product = productService.findById(received.getProductId());
        Long categoryId = product.getCategory().getId();

        HashMap<Long, Long> map;
        if(actionScore == null) { // 결과가 없으면 객체 새로 생성해서 작업 진행
            actionScore = new CategoryScore();
            map = actionScore.getUserCategoryScore();
            map.put(categoryId, score); // 새 값 추가
        }else{ // 결과가 있는 상태라면
            map = actionScore.getUserCategoryScore(); // 가져오기
            // 키 값이 있는지도 확인했어야 했다!
            if(map.containsKey(categoryId)){ // 키 있다면
                map.put(categoryId, map.get(categoryId)+score); // 기존값에 추가
            }else{ // 키 없다면
                map.put(categoryId, score); // 새 값 추가
            }
        }

        // 인덱스는 직접 생성했다

        // 문서 추가
        putESUserAction(id, actionScore); // 카테고리 점수

        // 사용자 행동 수집 -- ES 저장
        ClickProductAction clickProductAction = new ClickProductAction(received.getUserId(),received.getProductId(),received.getActionType());
        IndexQuery indexQuery = new IndexQueryBuilder()
                .withId(id + clickProductAction.getNow()) // _id : userId
                .withObject(clickProductAction) // class string?
                .build();
        log.info("\n click : " + esTemplate.index(indexQuery) + "\n");

    }

    public void putESUserAction(String id, CategoryScore userAction) {
        IndexQuery indexQuery = new IndexQueryBuilder()
                .withId(id) // _id
                .withObject(userAction) // list string
                .build();
        log.info("\n cateogry score : " + esTemplate.index(indexQuery) + "\n");
    }

    public CategoryScore getESUserActionById(String id) {
        return esTemplate.queryForObject(
                    GetQuery.getById(id), CategoryScore.class);
    }


    public void saveSearchData(SearchActionRequestDto requestDto) {
//        String actionType = requestDto.getActionType();

        SearchText allUserData = new SearchText(requestDto.getUserId(), requestDto.getSearchText());

        // es index save
        IndexQuery indexQuery = new IndexQueryBuilder()
                .withId(allUserData.getUserId()) // _id
                .withObject(allUserData) // list string
                .build();
        log.info("\nsearch data : " + esTemplate.index(indexQuery) + "\n");

    }


}
