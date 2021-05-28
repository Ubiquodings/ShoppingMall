package com.ubic.shop.dto;

import com.ubic.shop.domain.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Builder
@ToString
public class OrderProductResponseDto {
    private Long id;
    private String name;
    private int price;
    private long stockQuantity;
    private Long categoryId;
    private String description;
    private String imgUrl;
    private long count;

    public OrderProductResponseDto(Product entity, long count) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.price = entity.getPrice();
        this.stockQuantity = entity.getStockQuantity();
        this.count = count;
        categoryId = entity.getCategory().getId();
        description = entity.getDescription();
        imgUrl = entity.getImgUrl();
    }

}
