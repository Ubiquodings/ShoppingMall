package com.ubic.shop.service;

import com.ubic.shop.config.UbicConfig;
import com.ubic.shop.domain.Product;
import com.ubic.shop.dto.ProductIdListResponseDto;
import com.ubic.shop.elasticsearch.service.ElasticSearchService;
import com.ubic.shop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class RecommendService {

    private final ElasticSearchService elasticSearchService;
//    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final UbicConfig ubicConfig;
    private final RestTemplate restTemplate;

    public List<Product> getRecommendList(String userId, String page) {

        // 페이징
        PageRequest pageRequest = PageRequest.of(/*0*/Integer.parseInt(page), ubicConfig.productDetailPageSize, Sort.by(Sort.Direction.DESC, "name"));

        List<Product> productList = productRepository.findByCategoryId(getHighestCategoryId(userId), pageRequest).getContent();

        // 가져온 카테고리가 없다면 {하드코딩} 카테고리의 상품 4개 출력하기 - category id: 907001
        return productList;
    }

    public Long getHighestCategoryId(String userId) {
        return elasticSearchService.getESUserActionById(userId);
    }

    public List<Product> getCategoryBaseRecommendList(String page, Long categoryId) {

        // 페이징 --
        PageRequest pageRequest = PageRequest.of(/*0*/Integer.parseInt(page), ubicConfig.productDetailPageSize, Sort.by(Sort.Direction.DESC, "name"));

        List<Product> productList = productRepository.findByCategoryId(categoryId, pageRequest).getContent();

        // 가져온 카테고리가 없다면 {하드코딩} 카테고리의 상품 4개 출력하기 - category id: 907001
        return productList;
    }

    public List<Product> getTestRecommendList(long page) {
        ProductIdListResponseDto result = null;
        try {
            result = restTemplate.getForObject(
                    ubicConfig.getDjangoServerUrl()+
                            "/cf/get-product-ids/",
                    ProductIdListResponseDto.class);

        } catch (Exception e) {
            log.info("\ndjango 요청이 실패하였습니다\n"+e.getMessage());
//            return 1L;
        }
        if (result == null) {
            return null;
        }

        log.info("\nDjango 결과 로깅합니다: "+result.toString()); // ok

        // 페이징
        PageRequest pageRequest = PageRequest.of(/*0*/(int)page, ubicConfig.productDetailPageSize, Sort.by(Sort.Direction.DESC, "name"));

        List<Long> productIdList = result.getProductIdList();
        Page<Product> byProductIdList = productRepository.findByProductIdList(productIdList, pageRequest);
        // 쿼리문이 잘 실행되는지 확인해야 한다 oo 아주 잘 온다!

        return byProductIdList.getContent();
    }
}
