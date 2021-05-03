package com.ubic.shop.repository.user_number;

import com.ubic.shop.domain.Category;
import com.ubic.shop.domain.coupon.Coupon;
import com.ubic.shop.domain.user_number.ProductViewUserNumber;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProductViewUserNumberRepository extends CrudRepository<ProductViewUserNumber, Long> {

    List<ProductViewUserNumber> findByProductId(Long productId);

    // 조건 user number > 0 : productId List 추출하기
    @Query(value = "select p.productId from ProductViewUserNumber p where p.userNumber > 0")
    List<Long> findProductIdByConUserNumber();
}
