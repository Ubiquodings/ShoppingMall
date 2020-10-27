package com.ubic.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@NoArgsConstructor // 기본생성자가 꼭 있어야 했다!
@AllArgsConstructor
@Getter
@ToString
public class ProductIdListResponseDto {
    List<Long> productIdList;

}
