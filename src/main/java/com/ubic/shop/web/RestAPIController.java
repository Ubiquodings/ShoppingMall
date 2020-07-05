package com.ubic.shop.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ubic.shop.config.LoginUser;
import com.ubic.shop.domain.Category;
import com.ubic.shop.domain.Product;
import com.ubic.shop.domain.ProductCategory;
import com.ubic.shop.dto.*;
import com.ubic.shop.repository.CategoryRepository;
import com.ubic.shop.service.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
public class RestAPIController {

    Logger logger = LoggerFactory.getLogger(RestAPIController.class);

    private final ProductService productService;
    private final ShopListService shopListService;
    private final OrderService orderService;
    private final ProductCategoryService productCategoryService;
    private final CategorySevice categoryService;
    private final KafkaSevice kafkaService;

    @PostMapping("/products/new")
    public ProductResponseDto save(@RequestBody ProductSaveRequestDto requestDto){
//        logger.info("\n"+requestDto.toString()+"\n"); // ok
        Category category = categoryService.getCategoryByKurlyId(requestDto.getCategoryId());
        if(category == null)
            return null;
//        Product product = requestDto.toEntity(category);
//        product.set
        return productService.saveProduct(requestDto, category);
    }

    @PostMapping("/categories/new")
    public CategoryResponseDto save(@RequestBody CategorySaveRequestDto requestDto){
//        logger.info("\n"+requestDto.toString());
        return new CategoryResponseDto(categoryService.saveCategory(requestDto));
    }

    // CartOrderRequestDto : Long productId
    @PostMapping("/carts/new/{id}") // TODO  restful 하진 않다
    public String cart(@PathVariable(name = "id") Long productId, @LoginUser SessionUser user) throws JsonProcessingException {
        if(user != null){
            String action = "cart";
            Product product = productService.findById(productId);
            kafkaService.sendToTopic(new UserActionKafkaRequestDto(user.getId(), action, product.getCategory().getId()));

            // 장바구니 저장
            shopListService.shopList(user.getId(), productId, 1);
        }
        return "{}";
    }
    // 빈 데이터 리턴도 json 형태로 해야 한다! -- 이유 :: https://vvh-avv.tistory.com/159

    @PostMapping("/orders/new/{id}") // TODO  restful 하진 않다
    public String order(@PathVariable(name = "id") Long productId, @LoginUser SessionUser user) throws JsonProcessingException {

        // TODO 주문 카프카 로직 추가 !!

        if(user != null){
            String action = "order";
            Product product = productService.findById(productId);
            kafkaService.sendToTopic(new UserActionKafkaRequestDto(user.getId(), action, product.getCategory().getId()));

            // 주문 저장
            orderService.order(user.getId(), productId, 1);
        }
        return "{}";
    }

    @GetMapping("/click/{id}")
    public String click(@PathVariable(name = "id") Long productId, @LoginUser SessionUser user) throws JsonProcessingException {
        if(user != null){
            String action = "click";
            logger.info("\n/click "+productId);
            Product product = productService.findById(productId);
            kafkaService.sendToTopic(new UserActionKafkaRequestDto(user.getId(), action, product.getCategory().getId()));
        }
        return "{}";
    }

    // 주문 취소 로직
    @DeleteMapping("/orders/{id}")
    public String detail(@PathVariable Long id, @LoginUser SessionUser user){
        orderService.cancelOrder(id);
//        if(user != null){
//            model.addAttribute("userName", user.getName());
//        }
        // 취소 어떻게 하랬더라 ?
        // order id 로 아이템 가져와서 remove ?
        return "{}";
    }

}
