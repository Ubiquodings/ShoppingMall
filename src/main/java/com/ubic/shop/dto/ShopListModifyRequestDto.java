package com.ubic.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ShopListModifyRequestDto {
    // 장바구니 id
    Long cartId;
    // 수량
    Long count;
}
