package com.ubic.shop.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ClickActionRequestDto {
    String userId;
    String actionType;
    //    Long categoryId;
    Long productId;
//    String searchText;

//    public ClickActionRequestDto(String userid, String actionType, Long categoryId) {
//        this.userid = userid;
//        this.actionType = actionType;
//        this.categoryId = categoryId;
//    }

    // @AllArgsConstructor 도 대체
//    public ClickActionRequestDto(String userid, String actionType, Long productId) {
//        this.userId = userid;
//        this.actionType = actionType;
//        this.productId = productId;
//    }

}
