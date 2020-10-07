package com.ubic.shop.config;

import com.ubic.shop.elasticsearch.domain.CategoryScore;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScheduledTasks {
    // 분석 결과는 NoSQL 에 1차적으로 저장해놔야 한다! < 데이터 결과 상황봐서 !

    private final RestTemplate restTemplate;
    private final UbicConfig ubicConfig;

//    @Scheduled(fixedDelay = 2000)
//    public void reportCurrentTime() {
//        log.info("test ScheduledTask !!"); // ok
//    }

    @Scheduled(fixedDelay = 2000)
    public void getDjangoData() {
        /* 임시로 가져올 데이터
        * 11794,6079,9694,20522,5607,8328,10121,45417,11809,46176,17745,18456,18616,45552,2171,5424,2095,2123,2000,6000,1071,987,5968,1045,1784,1016,2045,2140
        * */
        log.info("\ngetDjangoData: ");

        ProductIdListResponseDto result = null;
        try {
            result = restTemplate.getForObject(
                    ubicConfig.getDjangoServerUrl()+
                            "/cf/get-product-ids/",
                    ProductIdListResponseDto.class);

        } catch (Exception e) {
            log.info("\ndjango 요청이 실패하였습니다\n"+e.getMessage());
//            return 1L;
        }
        if (result == null) {
            return;
        }

        log.info("\nDjango 결과 로깅합니다: "+result.toString()); // ok

        List<Long> productIdList = result.getProductIdList();
        // List<Product> 가져와서 소켓 서버로 서빙한다쳐봐,
        // 소켓 url 은 뭔데 ?
    }

    @NoArgsConstructor // 기본생성자가 꼭 있어야 했다!
    @AllArgsConstructor
    @Getter @ToString
    static class ProductIdListResponseDto {
        List<Long> productIdList;
    }
}
