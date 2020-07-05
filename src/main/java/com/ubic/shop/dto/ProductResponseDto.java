package com.ubic.shop.dto;

import com.ubic.shop.domain.Product;
import lombok.Getter;

@Getter
public class ProductResponseDto {
    private Long id;
    private String name;
    private int price;
    private int stockQuantity;
    private Long categoryId;
    private String description;
    private String imgUrl;

    public ProductResponseDto(Product entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.price = entity.getPrice();
        this.stockQuantity = entity.getStockQuantity();
//        categoryId = entity.getCategory().getId();
        description=entity.getDescription();
        imgUrl = entity.getImgUrl();
    }
}
