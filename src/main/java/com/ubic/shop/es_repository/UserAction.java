package com.ubic.shop.es_repository;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.HashMap;
import java.util.Map;

//@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Document(indexName="ubic_user_action", type="_doc")
public class UserAction {

//    @Id
//    private String id; // data 채우면서 id 삽입하는 과정이 있으므로 멤버로 필요는 없을듯!
//    private String actionType;
//    private Long categoryId;
//    private Long score;
    private HashMap<Long,Long> userCategoryScore = new HashMap<>();
}
