package com.ubic.shop.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
//import org.springframework.data.annotation.Id;

import javax.persistence.*;
//@NoArgsConstructor
//@Entity
//@Getter
//@Setter(AccessLevel.PROTECTED)
//@Table(name = "product_category")
public class ProductCategory extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "product_category_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product; //주문 상품

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category; //주문

    //==생성 메서드==//
    public static ProductCategory createProductCategory(Product product, Category category) {
        ProductCategory productCategory = new ProductCategory();
//        productCategory.setProduct(product);
//        productCategory.setCategory(category);
        return productCategory;
    }
}
