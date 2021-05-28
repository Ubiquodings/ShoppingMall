package com.ubic.shop.elasticsearch.service;

import com.ubic.shop.config.UbicConfig;
import com.ubic.shop.elasticsearch.domain.CategoryScore;
import com.ubic.shop.elasticsearch.domain.ClickProductAction;
import com.ubic.shop.elasticsearch.domain.SearchText;
import com.ubic.shop.kafka.dto.ClickActionRequestDto;
import com.ubic.shop.kafka.dto.SearchActionRequestDto;
import com.ubic.shop.repository.ShopListRepository;
import com.ubic.shop.service.CouponService;
import com.ubic.shop.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ElasticSearchService {

    private final ElasticsearchRestTemplate esTemplate; // sb version up 2.2.x 로 새로 등장
    private final RestTemplate restTemplate;
    private final UbicConfig ubicConfig;

    public void updateCategoryScore(ClickActionRequestDto received) {

        // 사용자 행동 수집 -- ElasticSearch에 저장
        ClickProductAction clickProductAction = ClickProductAction.builder()
                .now(LocalDateTime.now().toString())
                .userId(received.getUserId().toString())
                .productId(received.getProductId())
                .categoryId(received.getCategoryId())
                .actionType(received.getActionType())
                .build();

        IndexQuery indexQuery = new IndexQueryBuilder()
                .withId(received.getUserId().toString() + clickProductAction.getNow()) // _id : userId
                .withObject(clickProductAction)
                .build();
        log.info("\nElasticSearch 에 저장합니다 : " + clickProductAction);
        esTemplate.index(indexQuery);

    }

    public long getESUserActionById(String userId) {

        CategoryScore result = null;
        try {
            result = restTemplate.getForObject(
                    ubicConfig.getDjangoServerUrl() +
                            "/cf/category-score/?index_name=ubic_click_action&user_id=" + userId,
                    CategoryScore.class);

        } catch (Exception e) {
            return 1L;
        }

        return result.getMaxScoreCategory(); // category id
    }


    public void saveSearchData(SearchActionRequestDto requestDto) {

        SearchText allUserData = new SearchText(requestDto.getUserId(), requestDto.getSearchText());

        // es index save
        IndexQuery indexQuery = new IndexQueryBuilder()
                .withId(allUserData.getUserId()) // _id
                .withObject(allUserData) // list string
                .build();
        esTemplate.index(indexQuery);
    }

}
