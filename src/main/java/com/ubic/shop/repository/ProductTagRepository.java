package com.ubic.shop.repository;

import com.ubic.shop.domain.ProductTag;
import com.ubic.shop.domain.Tag;
import org.springframework.data.repository.CrudRepository;

public interface ProductTagRepository extends CrudRepository<ProductTag, Long> {
}
