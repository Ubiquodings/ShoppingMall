package com.ubic.shop.dto;

import lombok.Getter;

@Getter
public class UserActionKafkaRequestDto {
    Long userid;
    String actionType;
    Long categoryId;

    public UserActionKafkaRequestDto(Long userid, String actionType, Long categoryId) {
        this.userid = userid;
        this.actionType = actionType;
        this.categoryId = categoryId;
    }
}
