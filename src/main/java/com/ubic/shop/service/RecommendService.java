package com.ubic.shop.service;

import com.ubic.shop.config.UbicConfig;
import com.ubic.shop.domain.Product;
import com.ubic.shop.elasticsearch.service.ElasticSearchService;
import com.ubic.shop.elasticsearch.domain.CategoryScore;
import com.ubic.shop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class RecommendService {

    private final ElasticSearchService elasticSearchService;
//    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final UbicConfig ubicConfig;

    public List<Product> getRecommendList(/*@LoginUser SessionUser user*/String userId, String page) {
        // es 에서 user 관련 데이터 중 - 쿼리실행
//        String id = user.getId().toString();

        Long categoryId = getHighestCategoryId(userId);
        List<Product> productList;

        // 페이징 --
        PageRequest pageRequest = PageRequest.of(/*0*/Integer.parseInt(page), ubicConfig.productDetailPageSize, Sort.by(Sort.Direction.DESC, "name"));

        productList = productRepository.findByCategoryId(categoryId, pageRequest).getContent();

        // 가져온 카테고리가 없다면 {하드코딩} 카테고리의 상품 4개 출력하기 - category id: 907001
        return productList;
    }

    public Long getHighestCategoryId(String userId) {

        return elasticSearchService.getESUserActionById(userId);
    }
}
