package com.ubic.shop.repository;

import com.ubic.shop.domain.ShopList;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ShopListRepository extends CrudRepository<ShopList, Long> {

    List<ShopList> findAllByUserId(Long userId);
}
