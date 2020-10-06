package com.ubic.shop.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubic.shop.config.LoginUser;
import com.ubic.shop.config.UbicConfig;
import com.ubic.shop.config.UbicSecretConfig;
import com.ubic.shop.domain.*;
import com.ubic.shop.domain.coupon.AllAroundCoupon;
import com.ubic.shop.domain.coupon.CategoryCoupon;
import com.ubic.shop.domain.coupon.Coupon;
import com.ubic.shop.domain.coupon.ProductCoupon;
import com.ubic.shop.domain.user_number.ProductOrderUserNumber;
import com.ubic.shop.dto.*;
import com.ubic.shop.kafka.dto.ClickActionRequestDto;
import com.ubic.shop.kafka.service.KafkaSevice;
import com.ubic.shop.repository.*;
import com.ubic.shop.service.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@RestController
@Slf4j
public class RestAPIController {

//    Logger logger = LoggerFactory.getLogger(RestAPIController.class);

    private final ProductService productService;
    private final ShopListService shopListService;
    private final ShopListRepository shopListRepository;
    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final ProductCategoryService productCategoryService;
    private final CategorySevice categoryService;
    private final KafkaSevice kafkaService;
    private final UserRepository userRepository;
    private final UbicSecretConfig ubicSecretConfig;
    private final UbicConfig ubicConfig;
    private final CouponRepository couponRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper;
    private final UserNumberService userNumberService;
    private final UserNumberBroadcastService userNumberBroadcastService;


    @PostMapping("/api/products/new")
    public ProductResponseDto save(@RequestBody ProductSaveRequestDto requestDto) {
//        logger.info("\n"+requestDto.toString()+"\n"); // ok
        Category category = categoryService.getCategoryByKurlyId(requestDto.getKurlyId());
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
    @PostMapping("/api/carts/new/{id}")
    public String cart(@PathVariable(name = "id") Long productId, @RequestBody long count,
                       @LoginUser SessionUser user, HttpServletRequest request) {

        log.info("\n여기는 서버 컨트롤러 cart()\nproductId: " + productId + "\ncount: " + count);

        Long clientId = -1L;
        clientId = getUserIdFromSession(user, request);
        Product product = productService.findById(productId);


        String action = "cart-create";
        kafkaService.buildKafkaRequest(clientId, product, action);

//        kafkaService.sendToTopic(new ClickActionRequestDto(clientId, action, product/*.getCategory()*/.getId()));

        //Long count = shopListService.findAllShopLists()
        // 장바구니 저장
        //shopListService.shopList(shopListUserId, productId, 1);
        shopListService.shopList(clientId, productId, count);

        return "{}";
    }

//    public void buildKafkaRequest(Long clientId, Product product, String action) {
//        ClickActionRequestDto requestDto = ClickActionRequestDto.builder()
//                .userId(clientId)
//                .actionType(action)
//                .categoryId(product.getCategory().getId())
//                .productId(product.getId())
//                .build();
//        try {
//            kafkaService.sendToTopic(requestDto);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//            return;
//        }
//    }

    public Long getUserIdFromSession(@LoginUser SessionUser user, HttpServletRequest request) {
        Long clientId;
        if (user != null) {
            clientId = user.getId();
//            shopListUserId = user.getId();
        } else {
//            clientId = request.getSession().getId();
            User nonMember = getTempUser(request);
            clientId = nonMember.getId();
//            shopListUserId = nonMember.getId();
        }
        return clientId;
    }
    // 빈 데이터 리턴도 json 형태로 해야 한다! -- 이유 :: https://vvh-avv.tistory.com/159

    // 상품 디테일에서 바로 주문 기능
    @PostMapping("/api/orders/fromDetail/{id}") // 상품 상세 페이지에서 바로 하나 주문하는 기능
    public String orderOneFromDetail(@PathVariable(name = "id") Long productId, @LoginUser SessionUser user,
                                     @RequestBody int count,
                                     HttpServletRequest request) throws JsonProcessingException {
        Long clientId = -1L;
        clientId = getUserIdFromSession(user, request);

        String action = "order-create";
        Product product = productService.findById(productId);

        kafkaService.buildKafkaRequest(clientId, product, action);

        // 상품 구매한 사용자 수 갱신
        userNumberBroadcastService.plusProductOrderUserNumber(product.getId(), 1L);

//        kafkaService.sendToTopic(new ClickActionRequestDto(clientId, action, product.getId()));

        // 주문 저장
        orderService.orderOneFromDetail(clientId, product.getId(), count);

        return "{}";

    }


    /*장바구니에서 주문하기 버튼을 누르면 실행된다*/
    @PostMapping("/api/orders/AllfromShopList") // 장바구니에서 여러개 주문하는 기능
    public String orderAllFromShopList(@RequestBody List<String> FromShopList,
                                       @LoginUser SessionUser user,
                                       HttpServletRequest request) throws JsonProcessingException {

        Long clientId = -1L;
        clientId = getUserIdFromSession(user, request);

        //String action = "order"; // 이건 어떤 역할을 하는거지?


        System.out.println("리스트 사이즈" + FromShopList.size());
        for (int i = 0; i < FromShopList.size(); i = i + 2) {

            System.out.println("==========================================================================");
            System.out.println(i + "FromShopList.get(i)" + FromShopList.get(i)); // shopList id
            System.out.println(i + "Long.parseLong(FromShopList.get(i))" + Long.parseLong(FromShopList.get(i)));
            System.out.println(i + "Integer.parseInt(FromShopList.get(i+1))" + Integer.parseInt(FromShopList.get(i + 1)));
            System.out.println("==========================================================================");
            Long shopListId_L = Long.parseLong(FromShopList.get(i)); // shopList id
            ShopList shopList = shopListRepository.findById(shopListId_L).get();
            Product product = shopList.getProduct();

            //정보가 없는데 가져오라고 한건가..? ㅎ..
            /*Payment payment_list = paymentRepository.findById(shopListId_L).get();
            Product product = payment_list.getProduct();*/

            //kafkaService.sendToTopic(new ClickActionRequestDto(clientId, action, product.getId()));

            /*int shopListCount_I = Integer.parseInt(shopListCount.get(i));*/
            Long shopListCount_I = Long.parseLong(FromShopList.get(i + 1)); // shopList item count

            // 주문 저장
            /*orderService.orderAllFromShopList(shopListUserId, product.getId(), shopListCount_I, shopListId_L);*/
            paymentService.payment(clientId, product.getId(), shopListCount_I);

        }//end for
        return "{}";
    }

    /*결재페이지에서 주문 api*/
    @PostMapping("/api/orderAll")
    public String orderAll(@RequestBody OrderAllRequestDto requestDto,
                           @LoginUser SessionUser user, HttpServletRequest request) throws JsonProcessingException {
        log.info("\n결제페이지 쿠폰: "+requestDto.toString());

        Long clientId = -1L;
        clientId = getUserIdFromSession(user, request);
        User userEntity;
        userEntity = userRepository.findById(clientId).get();
        final Long userId = userEntity.getId();

        // 모든 payment 객체 삭제하기 : 해당 회원의
        List<Payment> paymentAllByUserId = paymentRepository.findAllByUserId(clientId);
        paymentAllByUserId
                .forEach(payment -> paymentRepository.deleteById(payment.getId()));

        //장바구니 모든 객체 삭제하기 && Order 객체 생성
        List<ShopList> shopListAllByUserId = shopListRepository.findAllByUserId(clientId);
        List<OrderProduct> orderProductList = new ArrayList<>();
//        null;
        shopListAllByUserId
                .forEach(shopList -> {

                    Product product = shopList.getProduct();
                    // 사용자 로그 생성 : order
                    String action = "order-create";
                    kafkaService.buildKafkaRequest(userId, product, action);

                    // 상품 구매한 사용자 수 갱신
                    try {
                        userNumberBroadcastService.plusProductOrderUserNumber(product.getId(), 1L);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }

                    // order 객체 생성
                    OrderProduct orderProduct = OrderProduct.createOrderProduct(product, product.getPrice(), shopList.getCount());
                    orderProductList.add(orderProduct);
                    shopListRepository.deleteById(shopList.getId());

                });
        Order order = Order.createOrder(userEntity, orderProductList.toArray(new OrderProduct[0]));
        order.initTitleAndTotalPrice();
        orderService.save(order);

        // 체크된 쿠폰 삭제하기
        List<Coupon> couponByUserIdandIds = couponRepository.findByUserIdandIds(requestDto.getCouponIdList(), clientId);
        couponByUserIdandIds
                .forEach(coupon -> {
                    // TODO Kafka coupon-use

                    // 쿠폰 사용한 사용자 수 갱신
                    String couponType = coupon.getCouponType();
                    try {
                        log.info("\ncoupon use type : "+couponType);
                        userNumberBroadcastService.plusCouponUseUserNumber(couponType, 1L);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }

                    coupon.changeStatusUsed();
                    couponService.saveChangedCoupon(coupon); // 바뀌는지 : 기존 db 에는 status 가 하나도 바뀌지 않았다!
                });

        return "{}";
    }

    private final CouponService couponService;

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
            log.info("\nsession is not new");
            nonMember = userRepository.findByName(session.getId());
        }
        return nonMember;
    }


    @GetMapping("/api/click/{id}")
    public String click(@PathVariable(name = "id") Long productId, @LoginUser SessionUser user,
                        HttpServletRequest request) throws JsonProcessingException {

        log.info("\n\n click api");

        Long clientId = -1L;
        clientId = getUserIdFromSession(user, request);

        String action = "click";
        log.info("\n/click " + productId);
        Product product = productService.findById(productId);

        kafkaService.buildKafkaRequest(clientId, product, action);

        return "{}";
    }

    @GetMapping("/api/hover/{id}")
    public String hover(@PathVariable(name = "id") Long productId, @LoginUser SessionUser user,
                        HttpServletRequest request) throws JsonProcessingException {

        log.info("\n\n hover api");

        Long clientId = -1L;
        clientId = getUserIdFromSession(user, request);

        String action = "hover";
        log.info("\n/hover " + productId);
        Product product = productService.findById(productId);

        kafkaService.buildKafkaRequest(clientId, product, action);

//        kafkaService.sendToTopic(new ClickActionRequestDto(clientId, action, product/*.getCategory()*/.getId()));

        return "{}";
    }


    // 주문 취소 로직
    @DeleteMapping("/api/orders/{id}")
    public String cancelOrder(@PathVariable Long id, @LoginUser SessionUser user, HttpServletRequest request) {
        Long clientId = -1L;
        clientId = getUserIdFromSession(user, request);

        // service 단에서 order-cancel Kafka 수집 처리
        orderService.cancelOrder(id, clientId);
        return "{}";
    }

    // 장바구니 취소
    @DeleteMapping("/api/carts/{id}")
    public String cancelCartItem(@PathVariable Long id, @LoginUser SessionUser user, HttpServletRequest request) {
        Long clientId = -1L;
        clientId = getUserIdFromSession(user, request);

        // service 단에서 cart-cancel Kafka 수집 처리
        shopListService.cancelShopList(id, clientId);
        return "{}";
    }

    // 장바구니 수정
    @PutMapping("/api/carts")
    public String modifyCartItem(@RequestBody ShopListModifyRequestDto requestDto, /*@PathVariable Long id, */
                                 @LoginUser SessionUser user, HttpServletRequest request) {
        log.info("\n장바구니 수정: " + requestDto.getCartId());
        Long clientId = -1L;
        clientId = getUserIdFromSession(user, request);


        // service 단에서 cart-modify Kafka 수집 처리
        shopListService.modifyShopList(requestDto.getCartId(), requestDto.getCount(), clientId);
        return "{}";
    }

    // dashboard 에서 추천목록 가져오는 api 4가지
    @GetMapping("/api/recommendations/tmp")
    public List<RecommendationProductListResponseDto> getUserCfRecommendationList(
            @RequestParam(name = "page", defaultValue = "0") String page) throws JsonProcessingException {
        // 카테고리 id 전부 가져와서
        List<Long> allCategoryIdList = categoryRepository.getAllCategoryId();
        log.info("\ngetUserCfRecommendationList category id list 출력: " + allCategoryIdList.toString());
//        List<Long> randomCategoryId4 = new ArrayList<>();

        // 랜덤 id 추출 : 상품이 하나도 없는 카테고리가 있어서 안되겠다 !
//        Random rand = new Random(); //Long randomCategoryId;
        Long randomCategoryId = 3L;//allCategoryIdList.get(rand.nextInt(allCategoryIdList.size()));
        log.info("\ngetUserCfRecommendationList random category id  출력: " + randomCategoryId);

        // 각 category id 에 대해 4개씩 product List 를 가져온다
        // model 에 담아 화면에 전달한다
        PageRequest pageRequest = PageRequest.of(Integer.parseInt(page), ubicConfig.dashBoardProductListPageSize,
                Sort.by(Sort.Direction.DESC, "id"));

        Page<Product> productListByCategoryId = productRepository.findByCategoryId(randomCategoryId, pageRequest);
        log.info("\ngetUserCfRecommendationList 찾아온 product list size 출력: " + productListByCategoryId.getTotalElements());

        List<RecommendationProductListResponseDto> collect = productListByCategoryId.stream()
                .map((product) -> {
                    // Product > ProductTag > Tag 접근하는 로직
                    List<String> tagNameList = new ArrayList<>();
                    List<ProductTag> productTagList = product.getProductTagList();
                    for (ProductTag productTag : productTagList) {
                        tagNameList.add(productTag.getTag().getName());
                    }

                    return RecommendationProductListResponseDto.builder()
                            .productId(product.getId())
                            .productImgUrl(product.getImgUrl())
                            .productName(product.getName())
                            .productPrice(product.getPrice())
                            .productDescription(product.getDescription())

                            .tagNameList(tagNameList)
                            .categoryName(product.getCategory().getName())
                            .categoryId(product.getCategory().getId())
                            .build();

                }).collect(Collectors.toList());

        String result = objectMapper.writeValueAsString(collect);
        log.info("\n추천목록 하나 리턴: " + result);
        return collect;

    }

    @AllArgsConstructor
    @Getter /*@Setter*/
    @Builder
    @ToString
    static class RecommendationProductListResponseDto {
        Long productId;
        String productImgUrl;
        String productName;
        int productPrice;
        String productDescription;

        List<String> tagNameList;

        String categoryName;
        Long categoryId;
    }

    @GetMapping("/api/order-products/{id}")
    public List<OrderProductResponseDto> getOrderProducts(@PathVariable(name = "id") Long orderId, @LoginUser SessionUser user,
                                   HttpServletRequest request) throws JsonProcessingException {

        log.info("\n\n get order product list api");

        Optional<Order> byId = orderRepository.findById(orderId);
        if (!byId.isPresent()) {
            return new ArrayList<>();
        }

        Order order = byId.get();
        List<OrderProduct> orderProducts = order.getOrderProducts();
        List<OrderProductResponseDto> resultProductList = new ArrayList<>();
        orderProducts.forEach(orderProduct -> {
            resultProductList.add(new OrderProductResponseDto(orderProduct.getProduct(), orderProduct.getCount()));
        });

        String result = objectMapper.writeValueAsString(resultProductList); // 로그 작성 용
        log.info("\n추천목록 하나 리턴: " + result);

        return resultProductList;

    }


//    public List<Tag> TagNameListForRecommendation(List<ProductTag> productTagList){
//
//    }


//    @AllArgsConstructor
//    @Getter
//    static class TagNameListForRecommendation {
//        List<String> tagNameList;
//
//    }


    /*없어졌다!*/
//    @PostMapping("/api/orders/fromShopList/{id}") // 장바구니에서 바로 하나 주문하는 기능
//    public String orderFromShopList(@PathVariable(name = "id") Long shopListId, @LoginUser SessionUser user,
//                                    @RequestBody int count,
//                                    HttpServletRequest request) throws JsonProcessingException {
//
//        Long clientId = -1L;
//        clientId = getUserIdFromSession(user, request);
//
//        String action = "order";
//        ShopList shopList = shopListRepository.findById(shopListId).get();
//        Product product = shopList.getProduct();
//
//        ClickActionRequestDto requestDto = ClickActionRequestDto.builder()
//                .userId(clientId)
//                .actionType(action)
//                .categoryId(product.getCategory().getId())
//                .productId(product.getId())
//                .build();
//        kafkaService.sendToTopic(requestDto);
//
////        kafkaService.sendToTopic(new ClickActionRequestDto(clientId, action, product.getId()));
//
//        // 주문 저장
//        orderService.orderOneFromShopList(clientId, product.getId(), count, shopListId);
//
//        return "{}";
//    }


}

