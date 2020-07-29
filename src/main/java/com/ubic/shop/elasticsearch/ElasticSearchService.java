package com.ubic.shop.elasticsearch;

import com.ubic.shop.domain.Product;
import com.ubic.shop.elasticsearch.domain.ClickProductAction;
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

        // 기존 es 객체 가져와야 한다 TODO null 체크해야 한다! ?  또한 만약 새로운 고객이면 .. ?
        String id = received.getUserId();
        CategoryScore actionScore = getESUserActionById(id);
//        Map<Long,Long> map = objectMapper.readValue(jsonMapValue, new TypeReference<Map<Long,Long>>(){});

        // score update
//        if(userAction == null){ // 해당 id 없으면 null
//            log.info("\nelastic result is null !!");
//        }
        Product product = productService.findById(received.getProductId());
        Long categoryId = product.getCategory().getId();

        HashMap<Long, Long> map;
        if(actionScore == null) { // 결과가 없으면 객체 새로 생성해서 작업 진행
//            map = userAction.getUserCategoryScore();
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
//        HashMap<Long, Long> map = userAction.getUserCategoryScore();


        // 엘라스틱 서치에 저장 : Map to json
//        String updatedJsonValue = objectMapper.writeValueAsString(map);

        // 인덱스는 직접 생성했다

        // 문서 추가
        putESUserAction(id, actionScore); // 카테고리 점수

        // 사용자 행동 수집
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

        SearchText allUserData = null;
        allUserData = new SearchText(requestDto.getUserId(), requestDto.getSearchText());

    }
}
