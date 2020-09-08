package com.ubic.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class ShopListModifyRequestDto {
    // 장바구니 id
    Long cartId;
    // 수량
    Long count;
}
