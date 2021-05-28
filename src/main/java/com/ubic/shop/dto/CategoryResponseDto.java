package com.ubic.shop.dto;

import com.ubic.shop.domain.Category;
import lombok.Getter;

@Getter
public class CategoryResponseDto {
    private Long kurlyId;
    private String name;

    public CategoryResponseDto(Category entity) {
        this.kurlyId = entity.getKurlyId();
        this.name = entity.getName();
    }
}
