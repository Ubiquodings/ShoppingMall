package com.ubic.shop.repository.user_number;

import com.ubic.shop.domain.Category;
import com.ubic.shop.domain.coupon.Coupon;
import com.ubic.shop.domain.user_number.ProductViewUserNumber;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProductViewUserNumberRepository extends CrudRepository<ProductViewUserNumber, Long> {

    List<ProductViewUserNumber> findByProductId(Long productId);

}
