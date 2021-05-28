package com.ubic.shop.dto;

import com.ubic.shop.domain.Category;
import com.ubic.shop.domain.Product;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductSaveRequestDto {

    private String name;
    private int price;
    private int stockQuantity;
    private String description;
    private String imgUrl;

    private Long kurlyId;

    public Product toEntity(Category category) {

        return Product.createProduct(name, price, stockQuantity, description, imgUrl, category);
    }

    @Builder
    public ProductSaveRequestDto(
            Long kurlyId,
            String name,
            int price,
            int stockQuantity,
            String description,
            String imgUrl
    ) {

        this.kurlyId = kurlyId;
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.description = description;
        this.imgUrl = imgUrl;
    }

}
