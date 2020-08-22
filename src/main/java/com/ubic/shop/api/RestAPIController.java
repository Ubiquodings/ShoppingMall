package com.ubic.shop.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ubic.shop.config.LoginUser;
import com.ubic.shop.config.UbicConfig;
import com.ubic.shop.config.UbicSecretConfig;
import com.ubic.shop.domain.*;
import com.ubic.shop.dto.*;
import com.ubic.shop.kafka.dto.ClickActionRequestDto;
import com.ubic.shop.kafka.service.KafkaSevice;
import com.ubic.shop.repository.ShopListRepository;
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
    private final ShopListRepository shopListRepository;
    private final OrderService orderService;
    private final ProductCategoryService productCategoryService;
    private final CategorySevice categoryService;
    private final KafkaSevice kafkaService;
    private final UserRepository userRepository;
    private final UbicSecretConfig ubicConfig;


    @PostMapping("/api/products/new")
    public ProductResponseDto save(@RequestBody ProductSaveRequestDto requestDto) {
//        logger.info("\n"+requestDto.toString()+"\n"); // ok
        Category category = categoryService.getCategoryByKurlyId(requestDto.getCategoryId());
        if (category == null)
            return null;
//        Product product = requestDto.toEntity(category);
//        product.set
        return productService.saveProduct(requestDto, category);
    }

    @PostMapping("/api/categories/new")
    public CategoryResponseDto save(@RequestBody CategorySaveRequestDto requestDto) {
//        logger.info("\n"+requestDto.toString());
        return new CategoryResponseDto(categoryService.saveCategory(requestDto));
    }

    // CartOrderRequestDto : Long productId
    @PostMapping("/api/carts/new/{id}") // TODO  restful 하진 않다
    /*public String cart(@PathVariable(name = "id") Long productId, @LoginUser SessionUser user,
                       HttpServletRequest request) throws JsonProcessingException {*/
    public String cart(@PathVariable(name = "id") Long productId, @RequestBody int count,
                       @LoginUser SessionUser user, HttpServletRequest request) throws JsonProcessingException {

        log.info("\n" + productId + "\n" + count);

        String clientId = null;
        Long shopListUserId;

        if (user != null) {
            clientId = user.getId().toString();
            shopListUserId = user.getId();
        } else {
            clientId = request.getSession().getId();
            User nonMember = getTempUser(request);
            shopListUserId = nonMember.getId();
        }

        String action = "cart";
        Product product = productService.findById(productId);
        kafkaService.sendToTopic(new ClickActionRequestDto(clientId, action, product/*.getCategory()*/.getId()));

        //Long count = shopListService.findAllShopLists()
        // 장바구니 저장
        //shopListService.shopList(shopListUserId, productId, 1);
        shopListService.shopList(shopListUserId, productId, count);

        return "{}";
    }
    // 빈 데이터 리턴도 json 형태로 해야 한다! -- 이유 :: https://vvh-avv.tistory.com/159


    @PostMapping("/api/orders/fromDetail/{id}") // 상품 상세 페이지에서 바로 하나 주문하는 기능
    public String orderOneFromDetail(@PathVariable(name = "id") Long productId, @LoginUser SessionUser user,
                                     @RequestBody int count,
                                     HttpServletRequest request) throws JsonProcessingException {
        String clientId = null;
        Long shopListUserId;
        if (user != null) {
            clientId = user.getId().toString();
            shopListUserId = user.getId();
        } else {
            clientId = request.getSession().getId();
            User nonMember = getTempUser(request);
            shopListUserId = nonMember.getId();
        }

        String action = "order";
        Product product = productService.findById(productId);

        kafkaService.sendToTopic(new ClickActionRequestDto(clientId, action, product.getId()));

        // 주문 저장
        orderService.orderOneFromDetail(shopListUserId, product.getId(), count);

        return "{}";

    }

    @PostMapping("/api/orders/fromShopList/{id}") // 장바구니에서 바로 하나 주문하는 기능
    public String orderFromShopList(@PathVariable(name = "id") Long shopListId, @LoginUser SessionUser user,
                                       @RequestBody int count,
                                     HttpServletRequest request) throws JsonProcessingException {

        String clientId = null;
        Long shopListUserId;
        if (user != null) {
            clientId = user.getId().toString();
            shopListUserId = user.getId();
        } else {
            clientId = request.getSession().getId();
            User nonMember = getTempUser(request);
            shopListUserId = nonMember.getId();
        }

        String action = "order";
        ShopList shopList = shopListRepository.findById(shopListId).get();
        Product product = shopList.getProduct();

        kafkaService.sendToTopic(new ClickActionRequestDto(clientId, action, product.getId()));

        // 주문 저장
        orderService.orderOneFromShopList(shopListUserId, product.getId(), count, shopListId);

        return "{}";
    }

    @PostMapping("/api/orderAll") // TODO  restful 하진 않다
    public String orderAll(OrderAllRequestDto requestDto,
                           @LoginUser SessionUser user, HttpServletRequest request) throws JsonProcessingException {

        return "{}";
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


    @GetMapping("/api/click/{id}")
    public String click(@PathVariable(name = "id") Long productId, @LoginUser SessionUser user,
                        HttpServletRequest request) throws JsonProcessingException {

        log.info("\n\n click api");

        String clientId = null;
        if (user != null) {
            clientId = user.getId().toString();
        } else {
            clientId = request.getSession().getId();
            log.info("\n\n click api session id" + clientId);
        }

        String action = "click";
        logger.info("\n/click " + productId);
        Product product = productService.findById(productId);

        kafkaService.sendToTopic(new ClickActionRequestDto(clientId, action, product/*.getCategory()*/.getId()));

        return "{}";
    }

    @GetMapping("/api/hover/{id}")
    public String hover(@PathVariable(name = "id") Long productId, @LoginUser SessionUser user,
                        HttpServletRequest request) throws JsonProcessingException {

        log.info("\n\n hover api");

        String clientId = null;
        if (user != null) {
            clientId = user.getId().toString();
        } else {
            clientId = request.getSession().getId();
            log.info("\n\n click api session id" + clientId);
        }

        String action = "hover";
        logger.info("\n/hover " + productId);
        Product product = productService.findById(productId);

        kafkaService.sendToTopic(new ClickActionRequestDto(clientId, action, product/*.getCategory()*/.getId()));

        return "{}";
    }


    // 주문 취소 로직
    @DeleteMapping("/api/orders/{id}")
    public String cancelOrder(@PathVariable Long id, @LoginUser SessionUser user) {
        orderService.cancelOrder(id);
//        if(user != null){
//            model.addAttribute("userName", user.getName());
//        }
        // 취소 어떻게 하랬더라 ?
        // order id 로 아이템 가져와서 remove ?
        return "{}";
    }

    // 장바구니 취소
    @DeleteMapping("/api/carts/{id}")
    public String cancelCartItem(@PathVariable Long id, @LoginUser SessionUser user) {
        shopListService.cancelShopList(id);
        return "{}";
    }

    // 장바구니 수정
    @PutMapping("/api/carts/{id}")
    public String modifyCartItem(ShopListModifyRequestDto requestDto, @PathVariable Long id, @LoginUser SessionUser user) {
        shopListService.modifyShopList(requestDto.getCartId(), requestDto.getCount());
        return "{}";
    }


    @GetMapping("/api/search")
    public String search(@RequestParam("keyword") String keyword, @LoginUser SessionUser user,
                         HttpServletRequest request) throws JsonProcessingException {

        log.info("\nkeyword: "+keyword+"\napi key: "+ubicConfig.etriApiKey); // ok

        //회원+비회원

        // 카프카에 전송하고 > 컨슈머 처리

        // 결과 보여줘야지.. @Controller

        return "{}";
    }
}
