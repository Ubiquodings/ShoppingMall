package com.ubic.shop.kafka.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
public class ClickActionRequestDto {
    Long userId;
    String actionType;
    Long categoryId;
    Long productId;
}
