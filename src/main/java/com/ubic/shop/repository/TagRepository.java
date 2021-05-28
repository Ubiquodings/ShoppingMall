package com.ubic.shop.repository;

import com.ubic.shop.domain.Tag;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TagRepository extends CrudRepository<Tag, Long> {

    List<Tag> findByName(String name);
}
