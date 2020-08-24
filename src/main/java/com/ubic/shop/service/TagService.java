package com.ubic.shop.service;

import com.ubic.shop.domain.Product;
import com.ubic.shop.domain.ProductTag;
import com.ubic.shop.domain.Tag;
import com.ubic.shop.dto.SearchResponseDto;
import com.ubic.shop.repository.ProductTagRepository;
import com.ubic.shop.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final ProductTagRepository productTagRepository;
    private final RestTemplate restTemplate;

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

    //    @Async
    private List<String> stemmingProductInfo(String productInfo) { // 상품 이름&설명 일반화할 수 있지 않을까!

        // url 요청 구성
        SearchResponseDto result = null;
        try {
            result = restTemplate.getForObject(
                    "http://127.0.0.1:8000/search/test/?text=" + productInfo,
                    SearchResponseDto.class);
        } catch (Exception e) {
            return null;
        }

//        if (result == null)
//            return null;

        // 스트림 처리하며 lemma 부분을 태그로 등록하기
        // Product 객체 필요한데! : ProductService 에서 처리!
        List<String> stemmingResult = result.getResult().stream()
                .map(m -> m.getLemma())
                .collect(Collectors.toList());

        return stemmingResult;

    }

}
