package com.ubic.shop.domain;
import com.ubic.shop.exception.NotEnoughStockException;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Entity
@Getter
@Setter(AccessLevel.PROTECTED)
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "product_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity=50;

    private String description;
    private String imgUrl;

//    @OneToMany
//    @JoinColumn(name = "order_id")
//    private List<ProductCategory> productCategories = new ArrayList<>();
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;


    //==연관관계 메서드==//
    public void addProductCategory(ProductCategory productCategory) {
//        productCategories.add(productCategory);
//        productCategory.setProduct(this);
    }

    //==비즈니스 로직==//
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0) {
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }

    // Entity 함부로 만들지 말라고 했다 ?
    @Builder
    public Product(String name, int price, int stockQuantity,
                   String description, String imgUrl,
                   Category category) {
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.description = description;
//        this.category.setId(categoryId); // 초기화 전 값 할당했다고 오류났다
        this.category = category; // 이렇게가 안먹히네!
        this.imgUrl = imgUrl;
    }
/*
*     //==생성 메서드==//
    public static OrderProduct createOrderProduct(Product product, int orderPrice, int count) {
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setProduct(product); //
        orderProduct.setOrderPrice(orderPrice);
        orderProduct.setCount(count);
        product.removeStock(count);
        return orderProduct;
    }

* */
    //==생성 메서드==//
    public static Product createProduct(String name, int price, int stockQuantity,
                                        String description, String imgUrl,
                                        Category category){
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setStockQuantity(stockQuantity);
        product.setDescription(description);
        product.setImgUrl(imgUrl);
        product.setCategory(category);
        return product;
    }

}
