package com.ubic.shop.elasticsearch.service;

import com.ubic.shop.config.UbicConfig;
import com.ubic.shop.domain.Product;
import com.ubic.shop.domain.ShopList;
import com.ubic.shop.dto.SearchResponseDto;
import com.ubic.shop.elasticsearch.domain.ClickProductAction;
import com.ubic.shop.elasticsearch.domain.ProductPageUserNumber;
import com.ubic.shop.kafka.dto.ClickActionRequestDto;
import com.ubic.shop.elasticsearch.domain.SearchText;
import com.ubic.shop.elasticsearch.domain.CategoryScore;
import com.ubic.shop.kafka.dto.SearchActionRequestDto;
import com.ubic.shop.repository.ShopListRepository;
import com.ubic.shop.service.CouponService;
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
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ElasticSearchService {

    private final ElasticsearchRestTemplate esTemplate; // sb version up 2.2.x 로 새로 등장 ?
    private final ProductService productService;
    private final ShopListRepository shopListRepository;
    private final CouponService couponService;
    private final RestTemplate restTemplate;
    private final UbicConfig ubicConfig;

    public void updateCategoryScore(ClickActionRequestDto received) {
        // 점수 장하는 로직 [시작]
//        String actionType = received.getActionType();
//
//        long score = 0;
//        switch (actionType){
//            case "click":
//                score = 1;
//                break;
//            case "cart":
//                score = 3;
//                break;
//            case "order":
//                score = 5;
//                break;
//        }
//
//        // 기존 es 객체 가져와야 한다
//        String id = received.getUserId().toString();
//        CategoryScore actionScore = getESUserActionById(id);
//
//        Product product = productService.findById(received.getProductId());
//        Long categoryId = product.getCategory().getId();
//
//        HashMap<Long, Long> map;
//        if(actionScore == null) { // 결과가 없으면 객체 새로 생성해서 작업 진행
//            actionScore = new CategoryScore();
//            map = actionScore.getUserCategoryScore();
//            map.put(categoryId, score); // 새 값 추가
//        }else{ // 결과가 있는 상태라면
//            map = actionScore.getUserCategoryScore(); // 가져오기
//            // 키 값이 있는지도 확인했어야 했다!
//            if(map.containsKey(categoryId)){ // 키 있다면
//                map.put(categoryId, map.get(categoryId)+score); // 기존값에 추가
//            }else{ // 키 없다면
//                map.put(categoryId, score); // 새 값 추가
//            }
//        }
//
//        // 인덱스는 직접 생성했다
//
//        // 문서 추가
////        putESUserAction(id, actionScore); // 카테고리 점수
//        IndexQuery indexQuery = new IndexQueryBuilder()
//                .withId(id) // _id
//                .withObject(actionScore) // list string
//                .build();
//        log.info("\n cateogry score : " + esTemplate.index(indexQuery) + "\n");

        // 점수저장하는 로직 [끝]

        // 사용자 행동 수집 -- ES 저장
        log.info("\n사용자행동 저장: 카테고리 "+received.getCategoryId());
        ClickProductAction clickProductAction = ClickProductAction.builder()
                .now(LocalDateTime.now().toString())
                .userId(received.getUserId().toString())
                .productId(received.getProductId())
                .categoryId(received.getCategoryId())
                .actionType(received.getActionType())
                .build();
        log.info("\n사용자행동 객체: "+clickProductAction.toString());
//        new ClickProductAction(,,);
        IndexQuery indexQuery = new IndexQueryBuilder()
                .withId(received.getUserId().toString() + clickProductAction.getNow()) // _id : userId
                .withObject(clickProductAction) // class string?
                .build();
        log.info("\n click : " + esTemplate.index(indexQuery) + "\n");

    }

//    public void putESUserAction(String id, CategoryScore userAction) {
//    }

    public long getESUserActionById(String userId) {
//        return esTemplate.queryForObject(
//                    GetQuery.getById(userId), CategoryScore.class);
        // es 에서 직접 가져오는 것이 아니라 장고 거쳐서 결과 받아온다
        log.info("\ndjango 에 es 분석결과 요청합니다 userId: "+userId);

        CategoryScore result = null;
        try {
            result = restTemplate.getForObject(
                    ubicConfig.getDjangoServerUrl()+
                    "/cf/category-score/?index_name=ubic_click_action&user_id=" + userId,
                    CategoryScore.class);

        } catch (Exception e) {
            log.info("\ndjango 에 es 분석결과 요청이 실패하였습니다\n"+e.getMessage());
            return 1L;
        }
        log.info("\ndjango 에 es 분석결과 로깅합니다: "+result.toString());

        return result.getMaxScoreCategory(); // category id
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
