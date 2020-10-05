package com.ubic.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UpdateCouponUserNumberDto {
    String couponType;
    long number;

}
