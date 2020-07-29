package com.ubic.shop.elasticsearch.domain;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.HashMap;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document(indexName="ubic_user_action", type="_doc")
public class CategoryScore { // 카테고리 점수를 저장하는 도메인

//    @Id
//    private String id; // data 채우면서 id 삽입하는 과정이 있으므로 멤버로 필요는 없을듯!
//    private String actionType;
//    private Long categoryId;
//    private Long score;
    private HashMap<Long,Long> userCategoryScore = new HashMap<>();
}
