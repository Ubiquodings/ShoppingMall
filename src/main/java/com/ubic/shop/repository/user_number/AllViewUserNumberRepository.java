package com.ubic.shop.repository.user_number;

import com.ubic.shop.domain.Category;
import com.ubic.shop.domain.user_number.AllViewUserNumber;
import com.ubic.shop.domain.user_number.ProductViewUserNumber;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AllViewUserNumberRepository extends CrudRepository<AllViewUserNumber, Long> {

    // All 가져와서 하나 꺼내쓰면 될듯
//    List<ProductViewUserNumber> findByProductId(Long productId);
}
