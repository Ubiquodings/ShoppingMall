package com.ubic.shop.service;

import com.ubic.shop.domain.ShopList;
import com.ubic.shop.domain.ShopListProduct;
import com.ubic.shop.domain.Product;
import com.ubic.shop.domain.User;
import com.ubic.shop.kafka.service.KafkaSevice;
import com.ubic.shop.repository.ShopListRepository;
import com.ubic.shop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ShopListService {

    private final UserRepository userRepository;
    private final ShopListRepository shopListRepository;
    private final ProductService productService;
    private final KafkaSevice kafkaService;

    @PersistenceContext
    EntityManager em;

    /** 장바구니 */
    @Transactional
    public Long shopList(Long userId, Long productId, long count) {
        //엔티티 조회
        User user = userRepository.findById(userId).get();
        Product product = productService.findById(productId);

        //주문상품 생성
//        ShopListProduct shopListProduct = ShopListProduct.createShopListProduct(product, product.getPrice(),
//                count);
        //주문 생성
        ShopList shopList = ShopList.builder()
                .user(user)
                .product(product)
                .count(count)
                .build();
        //주문 저장
        shopListRepository.save(shopList);

        //초기화
        em.flush();
        em.clear();

        return shopList.getId();
    }

    /** 장바구니 취소 */
    @Transactional
    public void cancelShopList(Long shopListId, Long clientId) {
        //취소
        Optional<ShopList> shopListbyId = shopListRepository.findById(shopListId);
        if(shopListbyId.isPresent()){
            String action = "cart-cancel";
            ShopList shopList = shopListbyId.get();
            kafkaService.buildKafkaRequest(clientId, shopList.getProduct(), action);

            // 객체 지우기
            shopListRepository.delete(shopList);
        }
        em.flush();
        em.clear();
    }

    /*장바구니 수정*/
    @Transactional
    public void modifyShopList(Long shopListId, Long count, Long userId){
//        log.info("\n장바구니 수정 Service: "+shopListId);
        if(shopListRepository.findById(shopListId).isPresent()){
            ShopList shopList = shopListRepository.findById(shopListId).get();
            log.info("\n장바구니 수정 Service: "+shopList.getId());
            long existCount = shopList.getCount();
            String action = null;
            if(existCount > count){ // cart-modify-plus
                action ="cart-modify-plus";
                kafkaService.buildKafkaRequest(userId, shopList.getProduct(), action);
            }else{ // cart-modify-minus
                action ="cart-modify-minus";
                kafkaService.buildKafkaRequest(userId, shopList.getProduct(), action);
            }
            shopList.changeCount(count); // 더티체킹 해주겠지 ?
        }else{
            return;
        }
    }

    public List<ShopList> findAllShopLists(Long userId) {

        return shopListRepository.findAllByUserId(userId);
    }
}