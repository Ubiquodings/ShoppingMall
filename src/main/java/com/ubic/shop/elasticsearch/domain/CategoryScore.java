package com.ubic.shop.elasticsearch.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CategoryScore { // 카테고리 점수를 저장하는 도메인

    long maxScoreCategory = -1L; // Id
}
