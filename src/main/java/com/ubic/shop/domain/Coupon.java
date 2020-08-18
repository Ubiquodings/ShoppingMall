package com.ubic.shop.domain;


import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@Entity
@Getter
@Setter(AccessLevel.PROTECTED)
public class Coupon extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "coupon_id")
    private Long id;
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Coupon(String name, User user) {
        this.name = name;
        this.user = user;
    }

}
