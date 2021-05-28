package com.ubic.shop.domain;

import lombok.*;

import javax.persistence.*;

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
    private Long kurlyId; // 얘가 실제 데이터 카테고리 id

    @Builder
    public Category(long kurlyId, String name) {
        this.kurlyId = kurlyId;
        this.name = name;
    }

}
