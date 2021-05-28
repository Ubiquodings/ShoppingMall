package com.ubic.shop.service;

import com.ubic.shop.config.UbicConfig;
import com.ubic.shop.domain.Product;
import com.ubic.shop.domain.ProductTag;
import com.ubic.shop.domain.Tag;
import com.ubic.shop.dto.SearchResponseDto;
import com.ubic.shop.repository.ProductTagRepository;
import com.ubic.shop.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class TagService {

    private final TagRepository tagRepository;
    private final ProductTagRepository productTagRepository;
    private final RestTemplate restTemplate;
    private final UbicConfig ubicConfig;

    @Transactional
    public void stemmingAndRegisterTag(Product product) {
        // 형태소 분석 - 상품 이름 : productName -- 제발 비동기 처리하자!
        List<String> result = stemmingProductInfo(product.getName());
        if (result == null)
            return;
        resgisterProductTag(product, result);

        // 형태소 분석 - 상품 설명 : productDesc
        result = stemmingProductInfo(product.getDescription());
        if (result == null)
            return;
        resgisterProductTag(product, result);
    }

    @Transactional
    public void resgisterProductTag(Product product, List<String> result) {
        Tag tag = null;
        for (String tagName : result) {
            List<Tag> findTag = tagRepository.findByName(tagName);
            if (findTag.isEmpty()) { // 같은 이름 태그 없으면 생성
                tag = Tag.builder()
                        .name(tagName)
                        .build();
                tag = tagRepository.save(tag);
            } else {// 같은 이름 태그 있으면 가져와서 이용
                tag = findTag.get(0);
            }
            ProductTag productTag = ProductTag.builder()
                    .product(product)
                    .tag(tag)
                    .build();
            productTag = productTagRepository.save(productTag);// 저장
            product.addProductTag(productTag); // 연관관계 설정
        }
    }

    public List<String> stemmingProductInfo(String productInfo) { // 상품 이름&설명 일반화할 수 있지 않을까!

        log.info("\n형태소 분석합니다: " + productInfo);

        // url 요청 구성
        SearchResponseDto result = null;
        try {
            result = restTemplate.getForObject(
                    ubicConfig.getDjangoServerUrl() + "/search/test/?text=" + productInfo,
                    SearchResponseDto.class);
        } catch (Exception e) {
            log.info("\n태그 분석 요청 실패 : null 을 반환합니다 !\n" + e.getMessage());
            return null;
        }

        // 스트림 처리하며 lemma 부분을 태그로 등록하기
        List<String> stemmingResult = null;
        if (result != null) {
            stemmingResult = result.getResult().stream()
                    .map(m -> {
                        log.info("\n형태소 분석 출력: " + m.getLemma());
                        return m.getLemma();
                    })
                    .collect(Collectors.toList());
            log.info("\n형태소 분석 결과: " + stemmingResult.size());
        }
        return stemmingResult;
    }

}
