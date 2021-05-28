package com.ubic.shop.config;

import com.ubic.shop.dto.ProductIdListResponseDto;
import com.ubic.shop.repository.UserRepository;
import com.ubic.shop.repository.user_number.ProductViewUserNumberRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScheduledTasks {

    private final RestTemplate restTemplate;
    private final UbicConfig ubicConfig;
    private final UserRepository userRepository;
    private final ProductViewUserNumberRepository productViewUserNumberRepository;

    @Builder
    @ToString
    private static class CFRequestDto {
        List<Long> requestUserIdList;
        List<Long> requestProductIdList;
    }


    @Scheduled(fixedDelay = 30 * 1000) // 30s
    public void getDjangoData() {
        log.info("\n4초 간격으로 데이터 분석 요청합니다");
        List<Long> requestUserIdList = userRepository
                .findUserIdByConTimeArrange(LocalDateTime.now(), LocalDateTime.now().minusMinutes(30L));
        List<Long> requestProductIdList = productViewUserNumberRepository.findProductIdByConUserNumber();

        CFRequestDto buildCFRequestDto = CFRequestDto.builder()
                .requestUserIdList(requestUserIdList)
                .requestProductIdList(requestProductIdList)
                .build();

        ProductIdListResponseDto result = null;
        try {
            restTemplate.postForObject(
                    ubicConfig.getDjangoServerUrl() + "",
                    buildCFRequestDto, Object.class);

            result = restTemplate.getForObject(
                    ubicConfig.getDjangoServerUrl() +
                            "/cf/get-product-ids1/",
                    ProductIdListResponseDto.class);

        } catch (Exception e) {
            log.info("\ndjango 요청이 실패하였습니다\n" + e.getMessage());
        }
        if (result == null) {
            return;
        }

    }

}
