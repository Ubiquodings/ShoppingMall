package com.ubic.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class OrderAllRequestDto {
    List<Long> couponIdList;
    List<Long> shopListIdList;
}
