package com.ubic.shop.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ubic.shop.config.LoginUser;
import com.ubic.shop.config.UbicConfig;

import com.ubic.shop.config.UbicSecretConfig;
import com.ubic.shop.domain.*;
import com.ubic.shop.dto.SessionUser;
import com.ubic.shop.kafka.dto.ClickActionRequestDto;
import com.ubic.shop.kafka.dto.SearchActionRequestDto;
import com.ubic.shop.kafka.service.KafkaSevice;
import com.ubic.shop.repository.ProductRepository;
import com.ubic.shop.repository.TagRepository;

import com.ubic.shop.domain.Product;
import com.ubic.shop.domain.Role;
import com.ubic.shop.domain.User;
import com.ubic.shop.dto.SessionUser;

import com.ubic.shop.repository.UserRepository;
import com.ubic.shop.service.ProductService;
import com.ubic.shop.service.RecommendService;
import com.ubic.shop.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Controller
@Slf4j
public class ProductController {

    private final ProductService productService;
    private final RecommendService recommendService;
    private final UbicConfig ubicConfig;
    private final UbicSecretConfig ubicSecretConfig;
    private final UserRepository userRepository;

    private final KafkaSevice kafkaService;
    private final TagService tagService;
    private final ProductRepository productRepository;
    private final TagRepository tagRepository;

//    private

    @GetMapping("/products")
    public String list(Model model, @LoginUser SessionUser user,
                       @RequestParam(name = "page", defaultValue = "0") String page, HttpServletRequest request) { // 화면 :: 윤진

//        User nonMember = getTempUser(request);

        // 사용 (age, offset=page, limit=40)
        // 정렬은 어떻게 하지 ? name 디폴트로 하고 정렬 라디오 박스 추가하면 되겠다 TODO
        PageRequest pageRequest = PageRequest.of(Integer.parseInt(page), ubicConfig.productListPageSize, Sort.by(Sort.Direction.DESC, "name"));
//        productService.findPagingProducts(pageRequest);

//        model.addAttribute("products", productService.findAllProducts(/*PageRequest.of(0,20)*/));
        model.addAttribute("products", productService.findPagingProducts(pageRequest)); // 40개씩 페이징

        if (user != null) {
            model.addAttribute("userName", user.getName());
        } else { // 해시코드 다섯글자만 추출하기
            User nonMember = getTempUser(request);
            model.addAttribute("clientId", nonMember.getName().substring(0, 5));
        }

        //끝페이지 가져오기
        Page<Product> pages=productRepository.findProductsCountBy(pageRequest);
        int page_total_count=pages.getTotalPages();
        model.addAttribute("page-total-count",page_total_count );
        return "product-list";

    }

    @GetMapping("/products/{id}")
    public String detail(@PathVariable Long id, Model model, @LoginUser SessionUser user,
                         @RequestParam(name = "page", defaultValue = "0") String page,
                         HttpServletRequest request) {

        model.addAttribute("product", productService.findById(id));


        String clientId = null;
        long userId = -1L;

        if (user != null) {
            model.addAttribute("userName", user.getName());
            clientId = user.getId().toString();
            userId = user.getId();
        } else { // 해시코드 다섯글자만 추출하기
            User nonMember = getTempUser(request);
            model.addAttribute("clientId", nonMember.getName().substring(0, 5));
            clientId = nonMember.getName();
            userId = nonMember.getId();
        }

        model.addAttribute("userId", userId);
        model.addAttribute("recommendedList", recommendService.getRecommendList(clientId, page));
        return "product-detail";
    }

    List<Tag> byName = new ArrayList<>();

    @GetMapping("/api/search")
    public String search(@RequestParam("keyword") String searchText, Model model,
                         @LoginUser SessionUser user, HttpServletRequest request) throws JsonProcessingException {

//        log.info("\nkeyword: "+searchText+"\napi key: "+ ubicSecretConfig.etriApiKey); // ok

        //회원+비회원
        Long userId = -1L;
        if (user != null) {
            model.addAttribute("userName", user.getName());
            userId = user.getId();
        } else { // 해시코드 다섯글자만 추출하기
            User nonMember = getTempUser(request);
            model.addAttribute("clientId", nonMember.getName().substring(0, 5));
            userId = nonMember.getId();
        }

        // 카프카에 전송하고 > 컨슈머 처리
        kafkaService.sendToTopic(new SearchActionRequestDto(userId.toString(), searchText));

        // 태그 이름 직접 검색하는 로직 추가
        List<Tag> tagListbyNameWithOriginalParam = tagRepository.findByName(searchText);
        List<Product> productListFromOriginalParam = getProductListFromTagList(tagListbyNameWithOriginalParam);

        // 검색어 형태소 분석
        List<String> result = tagService.stemmingProductInfo(searchText);
        if(result == null){
            model.addAttribute("products", productListFromOriginalParam); // 40개씩 페이징
            model.addAttribute("page-total-count",productListFromOriginalParam.size()%ubicConfig.productListPageSize );

            return "product-list";
        }
        // 한번에 찾아오는 기능 시도

        // String tagName > Tag
        List<Tag> byName = result.stream()
                .filter(x -> x!=null)
                .map(tagName -> {
                    log.info("\n debug tagName: "+tagName);
                    if(tagRepository.findByName(tagName).size() == 0) {
                        log.info("\ntag null");
                        return null;
                    }else {
                        log.info("\ntagName: "+tagRepository.findByName(tagName).get(0).getName());
                        return tagRepository.findByName(tagName).get(0); // 같은 이름 Tag 는 하나!
                    }
                })
                .filter(x -> x!=null)
                .collect(Collectors.toList());

        // byName + tagListbyNameWithOriginalParam
        byName = Stream.concat(byName.stream(), tagListbyNameWithOriginalParam.stream())
                .distinct() // 중복제거
                .collect(Collectors.toList());

        log.info("\nsearch TagbyName size: "+byName.size()); // 2 ok
        List<Product> searchResultProductList = getProductListFromTagList(byName);

        model.addAttribute("products", searchResultProductList); // 40개씩 페이징
        //끝페이지 가져오기
//        Page<Product> pages=productRepository.findProductsCountBy(pageRequest);
//        int page_total_count=pages.getTotalPages();
        model.addAttribute("page-total-count",searchResultProductList.size()%ubicConfig.productListPageSize );

        return "product-list";
    }

    public List<Product> getProductListFromTagList(List<Tag> tagList) {
        // Tag List > ProductTag List
        List<ProductTag> productTagList = new ArrayList<>();
        for(Tag tag : tagList){
            if(tag==null)
                log.info("\ntag list null"); // null
            else if (tag.getProductTagList().size() != 0) // null 일 수가 없어
                productTagList = Stream.concat(productTagList.stream(), tag.getProductTagList().stream())
                        .distinct()
                        .collect(Collectors.toList());
        }

        //ProductTag List > Product List
        return productTagList.stream()
                .map(ProductTag::getProduct)
                .distinct()
                .limit(ubicConfig.productListPageSize/*40*/)
                .collect(Collectors.toList());
    }

    private User getTempUser(HttpServletRequest request) {
        HttpSession session = request.getSession();
        User nonMember = null;
        if (session.isNew()) {
            log.info("\nsession is new : " + session.getId());
            // user 생성
            nonMember = User.builder()
                    .name(session.getId())
                    .email(null)
                    .picture(null)
                    .role(Role.GUEST)
                    .build();
            userRepository.save(nonMember);
        } else { // 새로운 세션이 아니라면 기존 세션이 있다는 말이니까!
            nonMember = userRepository.findByName(session.getId());
        }
        return nonMember;
    }


}