package com.ubic.shop.elasticsearch.domain;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Document(indexName = "ubic_recom_result", type = "_doc")
public class RecommendList {
    String now = LocalDateTime.now().toString();

    String userId;
    String recomType; // enum 으로 관리할까 ? .toString()
    List<Long> productIdList;
}
