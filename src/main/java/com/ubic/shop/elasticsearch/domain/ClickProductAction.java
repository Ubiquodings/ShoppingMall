package com.ubic.shop.elasticsearch.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document(indexName = "ubic_click_action", type = "_doc") // 아직 생성 안했다!
public class ClickProductAction {
    String now = LocalDateTime.now().toString();

    String userId;
    Long productId;
    String actionType;

    public ClickProductAction(String userId, Long productId, String actionType) {
        this.userId = userId;
        this.productId = productId;
        this.actionType = actionType;
    }
}
