package com.ubic.shop.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ubic.shop.config.LoginUser;
import com.ubic.shop.domain.Category;
import com.ubic.shop.domain.Product;
import com.ubic.shop.domain.Role;
import com.ubic.shop.domain.User;
import com.ubic.shop.dto.*;
import com.ubic.shop.kafka.dto.ClickActionRequestDto;
import com.ubic.shop.kafka.service.KafkaSevice;
import com.ubic.shop.repository.UserRepository;
import com.ubic.shop.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


@RequiredArgsConstructor
@RestController
@Slf4j
public class RestAPIController {

    Logger logger = LoggerFactory.getLogger(RestAPIController.class);

    private final ProductService productService;
    private final ShopListService shopListService;
    private final OrderService orderService;
    private final ProductCategoryService productCategoryService;
    private final CategorySevice categoryService;
    private final KafkaSevice kafkaService;
    private final UserRepository userRepository;


    @PostMapping("/api/products/new")
    public ProductResponseDto save(@RequestBody ProductSaveRequestDto requestDto){
//        logger.info("\n"+requestDto.toString()+"\n"); // ok
        Category category = categoryService.getCategoryByKurlyId(requestDto.getCategoryId());
        if(category == null)
            return null;
//        Product product = requestDto.toEntity(category);
//        product.set
        return productService.saveProduct(requestDto, category);
    }

    @PostMapping("/api/categories/new")
    public CategoryResponseDto save(@RequestBody CategorySaveRequestDto requestDto){
//        logger.info("\n"+requestDto.toString());
        return new CategoryResponseDto(categoryService.saveCategory(requestDto));
    }

    // CartOrderRequestDto : Long productId
    @PostMapping("/api/carts/new/{id}") // TODO  restful 하진 않다
    public String cart(@PathVariable(name = "id") Long productId, @LoginUser SessionUser user, HttpServletRequest request) throws JsonProcessingException {

        String clientId = null;
        Long shopListUserId;
        if(user != null){
            clientId = user.getId().toString();
            shopListUserId = user.getId();
        }else{
            clientId = request.getSession().getId();
            User nonMember = getTempUser(request);
            shopListUserId = nonMember.getId();
        }

        String action = "cart";
        Product product = productService.findById(productId);
        kafkaService.sendToTopic(new ClickActionRequestDto(clientId, action, product/*.getCategory()*/.getId()));

        // 장바구니 저장
        shopListService.shopList(shopListUserId, productId, 1);

        return "{}";
    }
    // 빈 데이터 리턴도 json 형태로 해야 한다! -- 이유 :: https://vvh-avv.tistory.com/159

    @PostMapping("/api/orders/new/{id}") // TODO  restful 하진 않다
    public String order(@PathVariable(name = "id") Long productId, @LoginUser SessionUser user, HttpServletRequest request) throws JsonProcessingException {

        String clientId = null;
        Long shopListUserId;
        if(user != null){
            clientId = user.getId().toString();
            shopListUserId = user.getId();
        }else{
            clientId = request.getSession().getId();
            User nonMember = getTempUser(request);
            shopListUserId = nonMember.getId();
        }

        String action = "order";
        Product product = productService.findById(productId);
        kafkaService.sendToTopic(new ClickActionRequestDto(clientId, action, product/*.getCategory()*/.getId()));

        // 주문 저장
        orderService.order(shopListUserId, productId, 1);

        return "{}";
    }

    private User getTempUser(HttpServletRequest request) {
        HttpSession session = request.getSession();
        User nonMember=null;
        if(session.isNew()){
            log.info("\nsession is new : "+session.getId());
            // user 생성
            nonMember = User.builder()
                    .name(session.getId())
                    .email(null)
                    .picture(null)
                    .role(Role.GUEST)
                    .build();
            userRepository.save(nonMember);
        }else{ // 새로운 세션이 아니라면 기존 세션이 있다는 말이니까!
            nonMember = userRepository.findByName(session.getId());
        }
        return nonMember;
    }


    @GetMapping("/api/click/{id}")
    public String click(@PathVariable(name = "id") Long productId, @LoginUser SessionUser user, HttpServletRequest request) throws JsonProcessingException {

        log.info("\n\n click api");

        String clientId = null;
        if(user != null){
            clientId = user.getId().toString();
        }else{
            clientId = request.getSession().getId();
            log.info("\n\n click api session id"+clientId);
        }

        String action = "click";
        logger.info("\n/click "+productId);
        Product product = productService.findById(productId);

        kafkaService.sendToTopic(new ClickActionRequestDto(clientId, action, product/*.getCategory()*/.getId()));

        return "{}";
    }

    // 주문 취소 로직
    @DeleteMapping("/api/orders/{id}")
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
