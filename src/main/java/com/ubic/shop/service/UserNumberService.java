package com.ubic.shop.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubic.shop.api.socket.SocketUserNumberController;
import com.ubic.shop.domain.user_number.AllViewUserNumber;
import com.ubic.shop.domain.user_number.CouponUseUserNumber;
import com.ubic.shop.domain.user_number.ProductOrderUserNumber;
import com.ubic.shop.domain.user_number.ProductViewUserNumber;
import com.ubic.shop.dto.UpdateCouponUserNumberDto;
import com.ubic.shop.dto.UpdateUserNumberDto;
import com.ubic.shop.repository.user_number.AllViewUserNumberRepository;
import com.ubic.shop.repository.user_number.CouponUseUserNumberRepository;
import com.ubic.shop.repository.user_number.ProductOrderUserNumberRepository;
import com.ubic.shop.repository.user_number.ProductViewUserNumberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserNumberService {

    //private final
    private final ProductViewUserNumberRepository productViewUserNumberRepository;
    private final AllViewUserNumberRepository allViewUserNumberRepository;
//    private final ProductOrderUserNumberRepository productOrderUserNumberRepository;
//    private final CouponUseUserNumberRepository couponUseUserNumberRepository;
//    private final SimpMessagingTemplate socketTemplate;
//    private final ObjectMapper objectMapper;


//    private final SocketUserNumberController socketUserNumberController;


    @Transactional // 소켓이랑 연결되어도 괜찮을까 ? 해보고 안되면 방법 찾기
    public void plusProductViewUserNumber(long productId, long number) { // +1, -1
        List<ProductViewUserNumber> byProductId = productViewUserNumberRepository.findByProductId(productId);
        ProductViewUserNumber productViewUserNumber;
        if (byProductId.size() != 0) { // 결과가 있다면 숫자 바꾸기
            productViewUserNumber = byProductId.get(0); // 객체 가져오기 & 연산
            productViewUserNumber.changeUserNumber(productViewUserNumber.getUserNumber() + number);

        } else { // 결과가 없다면: 객체 생성해서 저장
            log.info("\n해당상품 열람 사용자 없었음: " + productId);
            productViewUserNumber = ProductViewUserNumber.builder()
                    .productId(productId)
                    .userNumber(number)
                    .build();
            productViewUserNumberRepository.save(productViewUserNumber);
        }
        log.info("\n" + productViewUserNumber.toString());

        // 소켓-브로드캐스팅 결과 전달 : 필요가 없는게 send 요청으로 화면 갱신될거야
    }

    @Transactional
    public UpdateUserNumberDto getProductViewUserNumber(long productId) {
        List<ProductViewUserNumber> byProductId = productViewUserNumberRepository.findByProductId(productId);
        ProductViewUserNumber productViewUserNumber;
        if (byProductId.size() != 0) { // 결과가 있다면
            productViewUserNumber = byProductId.get(0);

        } else { // 결과없으면 새로 저장!
            productViewUserNumber = ProductViewUserNumber.builder()
                    .productId(productId)
                    .userNumber(1L)
                    .build();
            productViewUserNumberRepository.save(productViewUserNumber);
        }
        return new UpdateUserNumberDto(productViewUserNumber.getProductId(), productViewUserNumber.getUserNumber());
    }

    @Transactional
    public void plusAllViewUserNumber(long number) { // +1, -1
        Iterable<AllViewUserNumber> allViewUserNumberIterable = allViewUserNumberRepository.findAll();
        List<AllViewUserNumber> allViewUserNumberList = StreamSupport.stream(allViewUserNumberIterable.spliterator(), false)
                .collect(Collectors.toList());

        if (allViewUserNumberList.size() != 0) { // 결과가 있다면 숫자 바꾸기
            AllViewUserNumber allViewUserNumber = allViewUserNumberList.get(0); // 객체 가져오기 & 연산
            allViewUserNumber.changeUserNumber(allViewUserNumber.getUserNumber() + number);

        } else { // 결과가 없다면: 객체 생성해서 저장
            AllViewUserNumber allViewUserNumber = AllViewUserNumber.builder()
                    .userNumber(number)
                    .build();
            allViewUserNumberRepository.save(allViewUserNumber);
        }

        // 소켓-브로드캐스팅 결과 전달 : 필요가 없는게 send 요청으로 화면 갱신될거야

    }

    public long getAllViewUserNumber() {
        Iterable<AllViewUserNumber> allViewUserNumberIterable = allViewUserNumberRepository.findAll();
        List<AllViewUserNumber> allViewUserNumberList = StreamSupport.stream(allViewUserNumberIterable.spliterator(), false)
                .collect(Collectors.toList());

        if (allViewUserNumberList.size() != 0) { // 결과가 있다면 숫자 바꾸기
            return allViewUserNumberList.get(0).getUserNumber(); // 객체 가져오기 & 연산
        }
        return -1L;
    }


//    @Transactional
//    public void plusProductOrderUserNumber(long productId, long number) throws JsonProcessingException { // +1, -1
//        List<ProductOrderUserNumber> byProductId = productOrderUserNumberRepository.findByProductId(productId);
//
//        if (byProductId.size() != 0) { // 결과가 있다면 숫자 바꾸기
//            ProductOrderUserNumber productOrderUserNumber = byProductId.get(0); // 객체 가져오기 & 연산
//            productOrderUserNumber.changeUserNumber(productOrderUserNumber.getUserNumber() + number);
//
//        } else { // 결과가 없다면: 객체 생성해서 저장
//            ProductOrderUserNumber productOrderUserNumber = ProductOrderUserNumber.builder()
//                    .productId(productId)
//                    .userNumber(number)
//                    .build();
//            productOrderUserNumberRepository.save(productOrderUserNumber);
//        }
//
//        // TODO 소켓-브로드캐스팅 결과 전달 : 이게 되나 ? 안됩니다 (의존성 순환 문제)
////        socketUserNumberController.updateProductOrderUserNumber(productId, "");
////        broadcastProductOrderUserNumber(productId); // 의존성 순환 문제
//    }
//
//    public UpdateUserNumberDto getProductOrderUserNumber(long productId) {
//        List<ProductOrderUserNumber> byProductId = productOrderUserNumberRepository.findByProductId(productId);
//        if (byProductId.size() != 0) { // 결과가 있다면
//            ProductOrderUserNumber productOrderUserNumber = byProductId.get(0);
//            return new UpdateUserNumberDto(productOrderUserNumber.getProductId(), productOrderUserNumber.getUserNumber());
//        }
//        return null;
//    }
//
////    public void broadcastProductOrderUserNumber(long productId) throws JsonProcessingException {
////        // data 가져오기
////        UpdateUserNumberDto dto = getProductOrderUserNumber(productId);
////        if (dto == null){
////            log.info("\nreturn null");
////            dto = new UpdateUserNumberDto(productId, 0L); // 없으면 0 반환해야 한다!
////        }
////
////        String result = objectMapper.writeValueAsString(dto);
////        log.info("\nupdateOrderUserNumber : " + result);
////
////        /*해당 페이지 접속 사용자 수 브로드캐스트 갱신*/
////        socketTemplate.convertAndSend("/topic/users/ordered/" + productId, result);
////    }
//
//
//    @Transactional
//    public void plusCouponUseUserNumber(String couponType, long number) { // +1, -1
//        List<CouponUseUserNumber> byProductId = couponUseUserNumberRepository.findByCouponType(couponType);
//
//        if (byProductId.size() != 0) { // 결과가 있다면 숫자 바꾸기
//            CouponUseUserNumber couponUseUserNumber = byProductId.get(0); // 객체 가져오기 & 연산
//            couponUseUserNumber.changeUserNumber(couponUseUserNumber.getUserNumber() + number);
//
//        } else { // 결과가 없다면: 객체 생성해서 저장
//            CouponUseUserNumber couponUseUserNumber = CouponUseUserNumber.builder()
//                    .couponType(couponType)
//                    .userNumber(number)
//                    .build();
//            couponUseUserNumberRepository.save(couponUseUserNumber);
//        }
//
//        // TODO 소켓-브로드캐스팅 결과 전달
//
//    }
//
//    public UpdateCouponUserNumberDto getCouponUseUserNumber(String couponType) {
//        List<CouponUseUserNumber> byProductId = couponUseUserNumberRepository.findByCouponType(couponType);
//        if (byProductId.size() != 0) { // 결과가 있다면
//            CouponUseUserNumber couponUseUserNumber = byProductId.get(0);
//            return new UpdateCouponUserNumberDto(couponUseUserNumber.getCouponType(), couponUseUserNumber.getUserNumber());
//        }
//        return null;
//    }


}
