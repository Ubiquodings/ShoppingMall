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
