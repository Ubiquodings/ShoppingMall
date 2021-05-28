package com.ubic.shop.elasticsearch.domain;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Document(indexName = "ubic_click_action", type = "_doc")
public class ClickProductAction {
    String now = LocalDateTime.now().toString();

    String userId;
    Long productId;
    Long categoryId;
    String actionType;
}
