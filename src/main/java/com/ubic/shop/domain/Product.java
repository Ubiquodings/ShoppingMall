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
    private long stockQuantity = 5000000;

    private String description;
    private String imgUrl;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product") // ProductTag 쪽에서 Product 는 product 클래스멤버변수이름으로 관계가 설정되어 있다
    private List<ProductTag> ProductTagList = new ArrayList<>();


    //==연관관계 메서드==//
    public void addProductTag(ProductTag productTag) {
        this.ProductTagList.add(productTag);
    }

    //==비즈니스 로직==//
    public void addStock(long quantity) {
        this.stockQuantity += quantity;
    }

    public void removeStock(long quantity) {
        long restStock = this.stockQuantity - quantity;
        if (restStock < 0) {
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }

    @Builder
    public Product(String name, int price, int stockQuantity,
                   String description, String imgUrl,
                   Category category) {
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.description = description;
        this.category = category;
        this.imgUrl = imgUrl;
    }

    //==생성 메서드==//
    public static Product createProduct(String name, int price, int stockQuantity,
                                        String description, String imgUrl,
                                        Category category) {
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
