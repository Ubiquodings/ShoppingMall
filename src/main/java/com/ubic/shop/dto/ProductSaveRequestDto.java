package com.ubic.shop.dto;

import com.ubic.shop.domain.Category;
import com.ubic.shop.domain.Product;
import com.ubic.shop.domain.ProductCategory;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

//@Data
@Getter
@NoArgsConstructor
public class ProductSaveRequestDto {
    //id.name.price.stockQuantity.description.imgUrl
//    private Long id;
    private String name;
    private int price;
    private int stockQuantity;
    private String description;
    private String imgUrl;

//    private List<String> categoryList;
    private Long categoryId; // category 먼저 setting 하고, product input 할 때 category id 기반으로 삽입하기

    public Product toEntity(Category category) {
//        Category category =
        /*
        * String name, price, stockQuantity, description, imgUrl, category
        * */
        return Product.createProduct(name, price, stockQuantity, description, imgUrl, category);/*.builder()
                .name(name)
                .price(price)
                .stockQuantity(stockQuantity)
                .description(description)
                .imgUrl(imgUrl)
                .category(category)
//                .categoryId(categoryId) // 이게 된다고 ??
                .build();*/
    }

    @Builder
    public ProductSaveRequestDto(
            Long categoryId,
            String name,
            int price,
            int stockQuantity,
            String description,
            String imgUrl
    ) {

        this.categoryId = categoryId;
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.description = description;
        this.imgUrl = imgUrl;
    }

}
