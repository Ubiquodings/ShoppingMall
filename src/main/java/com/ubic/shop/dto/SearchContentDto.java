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
    Double weight; // 가중치가 좀 신경쓰이는데 일단 다 해보자!

}
