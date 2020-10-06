package com.ubic.shop.repository.user_number;

import com.ubic.shop.domain.Category;
import com.ubic.shop.domain.user_number.ProductOrderUserNumber;
import com.ubic.shop.domain.user_number.ProductViewUserNumber;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProductOrderUserNumberRepository extends CrudRepository<ProductOrderUserNumber, Long> {

    List<ProductOrderUserNumber> findByProductId(Long productId);
}
