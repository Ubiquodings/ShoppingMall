package com.ubic.shop.dto;

import com.ubic.shop.domain.Category;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CategorySaveRequestDto {
    private long kurlyId;
    private String name;

    public Category toEntity() {
        return Category.builder()
                .kurlyId(kurlyId)
                .name(name)
                .build();
    }

    @Builder
    public CategorySaveRequestDto(long kurlyId,String name) {
        this.kurlyId = kurlyId;
        this.name = name;
    }

}
