package com.ubic.shop.domain.user_number;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@NoArgsConstructor
@Entity
@Getter
@Setter(AccessLevel.PROTECTED)
public class AllViewUserNumber {
    long userNumber;

    @Id
    @GeneratedValue
    @Column(name = "all_view_user_number_id")
    private Long id;


    @Builder
    public AllViewUserNumber(long userNumber) {
        this.userNumber = userNumber;
    }

    public void changeUserNumber(long userNumber){
        this.userNumber = userNumber;
    }

}