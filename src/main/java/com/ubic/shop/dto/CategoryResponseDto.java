package com.ubic.shop.dto;

import com.ubic.shop.domain.Category;
import com.ubic.shop.domain.Product;
import lombok.Getter;

@Getter
public class CategoryResponseDto {
    private Long id;
    private String name;

    public CategoryResponseDto(Category entity) {
        this.id = entity.getId();
        this.name = entity.getName();
    }
}
