package com.ubic.shop.dto;

import com.ubic.shop.domain.Category;
import com.ubic.shop.domain.Product;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private Long kurlyId; // category 먼저 setting 하고, product input 할 때 category id 기반으로 삽입하기

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
