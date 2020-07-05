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
        ShopListProduct shopListProduct = ShopListProduct.createShopListProduct(product, product.getPrice(),
                count);
        //주문 생성
        ShopList shopList = ShopList.createShopList(user, /*delivery, */shopListProduct);
        //주문 저장
        shopListRepository.save(shopList);
        return shopList.getId();
    }
    
    /** 장바구니 취소 */
    @Transactional
    public void cancelShopList(Long shopListId) {
        //엔티티 조회
        ShopList shopList = shopListRepository.findOne(shopListId);
        //취소
        shopList.cancel();
    }

    public List<ShopList> findAllShopLists(Long userId) {
        return shopListRepository.findAll(userId);
    }
}
