package com.ubic.shop.domain;

import lombok.*;
//import org.springframework.data.annotation.Id;

import javax.persistence.*;

//import javax.persistence.Id;
@NoArgsConstructor
@Entity
@Getter @ToString
@Setter(AccessLevel.PROTECTED)
public class Category extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    private String name;
    private Long kurlyId; // 얘가 진짜 카테고리 id 인데!

//    @OneToOne(mappedBy = "category", fetch = FetchType.LAZY)
//    private Product product;

    @Builder
    public Category(long kurlyId, String name) {
        this.kurlyId = kurlyId;
        this.name = name;
    }

}
