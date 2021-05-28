package com.ubic.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SearchContentDto {
    Double id;
    String lemma;
    String type;
    Double position;
    Double weight; // 가중치
}
