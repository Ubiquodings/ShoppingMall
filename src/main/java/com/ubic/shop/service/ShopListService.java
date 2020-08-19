package com.ubic.shop.service;

import com.ubic.shop.domain.ShopList;
import com.ubic.shop.domain.ShopListProduct;
import com.ubic.shop.domain.Product;
import com.ubic.shop.domain.User;
import com.ubic.shop.repository.ShopListRepository;
import com.ubic.shop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ShopListService {

    private final UserRepository userRepository;
    private final ShopListRepository shopListRepository;
    private final ProductService productService;

    /** 장바구니 */
    @Transactional
    public Long shopList(Long userId, Long productId, int count) {
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
        return shopList.getId();
    }
    
    /** 장바구니 취소 */
    @Transactional
    public void cancelShopList(Long shopListId) {
        //취소
        shopListRepository.deleteById(shopListId);
    }

    /*장바구니 수정*/
    @Transactional
    public void modifyShopList(Long shopListId, Long count){
        ShopList shopList = shopListRepository.findById(shopListId).get();
        shopList.changeCount(count); // 더티체킹 해주겠지 ?
    }

    public List<ShopList> findAllShopLists(Long userId) {
        return shopListRepository.findAllByUserId(userId);
    }
}
