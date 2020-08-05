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
@Document(indexName="ubic_search_action", type="_doc") // 이전 : ubic_all_user_action
public class SearchText {
    String now = LocalDateTime.now().toString();

//    String actionType;
    String userId;
//    Long productId;
    String searchText;

    /*검색용*/
    public SearchText(String userId, String searchText){
//        this.actionType=actionType;
        this.userId=userId;
        this.searchText=searchText;
    }

//    /*클릭,장바구니,구매용*/ Coupon
//    public SearchText(String actionType, String userId, Long productId){
//        this.actionType=actionType;
//        this.userId=userId;
//        this.productId=productId;
//    }
}
