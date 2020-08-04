package com.ubic.shop.elasticsearch.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.HashMap;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document(indexName="ubic_product_user_number", type="_doc")
public class ProductPageUserNumber {
    private HashMap<Long,Long> userNumber = new HashMap<>(); // 인덱스 생성
}
