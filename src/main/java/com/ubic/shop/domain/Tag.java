package com.ubic.shop.domain;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Entity
@Getter
@Setter(AccessLevel.PROTECTED)
public class Tag extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "tag_id")
    private Long id;

    private String name;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "product_id")
//    private Product product;

    @OneToMany(mappedBy = "tag")
    private List<ProductTag> productTagList = new ArrayList<>();


    @Builder
    public Tag(/*Product product, */String name) {
//        this.product = product;
        this.name = name;
    }

    //==연관관계 메서드==//
    public void addTag(ProductTag productTag) {
        this.productTagList.add(productTag);
    }

}
