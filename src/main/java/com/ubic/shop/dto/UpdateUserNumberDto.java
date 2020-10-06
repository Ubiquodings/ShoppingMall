package com.ubic.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UpdateUserNumberDto {
    long productId = -1L;
    long number;

//    @Builder
//    public UpdateUserNumberDto(long productId, long number){
//        this.productId = productId;
//        this.number = number;
//    }

    @Builder
    public UpdateUserNumberDto(long number){
        this.number = number;
    }

}
