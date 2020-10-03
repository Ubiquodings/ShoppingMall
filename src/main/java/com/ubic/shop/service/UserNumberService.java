package com.ubic.shop.service;

import com.ubic.shop.domain.user_number.AllViewUserNumber;
import com.ubic.shop.domain.user_number.CouponUseUserNumber;
import com.ubic.shop.domain.user_number.ProductOrderUserNumber;
import com.ubic.shop.domain.user_number.ProductViewUserNumber;
import com.ubic.shop.repository.user_number.AllViewUserNumberRepository;
import com.ubic.shop.repository.user_number.CouponUseUserNumberRepository;
import com.ubic.shop.repository.user_number.ProductOrderUserNumberRepository;
import com.ubic.shop.repository.user_number.ProductViewUserNumberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final ProductOrderUserNumberRepository productOrderUserNumberRepository;
    private final CouponUseUserNumberRepository couponUseUserNumberRepository;


    @Transactional // 소켓이랑 연결되어도 괜찮을까 ? 해보고 안되면 방법 찾기
    public void plusProductViewUserNumber(long productId, long number) { // +1, -1
        List<ProductViewUserNumber> byProductId = productViewUserNumberRepository.findByProductId(productId);
        if(byProductId.size() != 0){ // 결과가 있다면 숫자 바꾸기
            ProductViewUserNumber productViewUserNumber = byProductId.get(0); // 객체 가져오기 & 연산
            productViewUserNumber.changeUserNumber(productViewUserNumber.getUserNumber()+number);

        }else{ // 결과가 없다면: 객체 생성해서 저장
            ProductViewUserNumber productViewUserNumber = ProductViewUserNumber.builder()
                    .productId(productId)
                    .userNumber(number)
                    .build();
            productViewUserNumberRepository.save(productViewUserNumber);
        }

        // TODO 소켓-브로드캐스팅 결과 전달
    }

    public long getProductViewUserNumber(long productId) {
        List<ProductViewUserNumber> byProductId = productViewUserNumberRepository.findByProductId(productId);
        if (byProductId.size() != 0) { // 결과가 있다면
            return byProductId.get(0).getUserNumber();
        }
        return -1L;
    }

    @Transactional
    public void plusAllViewUserNumber(long number) { // +1, -1
        Iterable<AllViewUserNumber> allViewUserNumberIterable = allViewUserNumberRepository.findAll();
        List<AllViewUserNumber> allViewUserNumberList = StreamSupport.stream(allViewUserNumberIterable.spliterator(), false)
                        .collect(Collectors.toList());

        if(allViewUserNumberList.size() != 0){ // 결과가 있다면 숫자 바꾸기
            AllViewUserNumber allViewUserNumber = allViewUserNumberList.get(0); // 객체 가져오기 & 연산
            allViewUserNumber.changeUserNumber(allViewUserNumber.getUserNumber()+number);

        }else{ // 결과가 없다면: 객체 생성해서 저장
            AllViewUserNumber allViewUserNumber = AllViewUserNumber.builder()
                    .userNumber(number)
                    .build();
            allViewUserNumberRepository.save(allViewUserNumber);
        }

        // TODO 소켓-브로드캐스팅 결과 전달

    }

    public long getAllViewUserNumber() {
        Iterable<AllViewUserNumber> allViewUserNumberIterable = allViewUserNumberRepository.findAll();
        List<AllViewUserNumber> allViewUserNumberList = StreamSupport.stream(allViewUserNumberIterable.spliterator(), false)
                .collect(Collectors.toList());

        if(allViewUserNumberList.size() != 0){ // 결과가 있다면 숫자 바꾸기
            return allViewUserNumberList.get(0).getUserNumber(); // 객체 가져오기 & 연산
        }
        return -1L;
    }


    @Transactional
    public void plusProductOrderUserNumber(long productId, long number) { // +1, -1
        List<ProductOrderUserNumber> byProductId = productOrderUserNumberRepository.findByProductId(productId);

        if(byProductId.size() != 0){ // 결과가 있다면 숫자 바꾸기
            ProductOrderUserNumber productOrderUserNumber = byProductId.get(0); // 객체 가져오기 & 연산
            productOrderUserNumber.changeUserNumber(productOrderUserNumber.getUserNumber()+number);

        }else{ // 결과가 없다면: 객체 생성해서 저장
            ProductOrderUserNumber productOrderUserNumber = ProductOrderUserNumber.builder()
                    .productId(productId)
                    .userNumber(number)
                    .build();
            productOrderUserNumberRepository.save(productOrderUserNumber);
        }

        // TODO 소켓-브로드캐스팅 결과 전달

    }

    public long getProductOrderUserNumber(long productId) {
        List<ProductOrderUserNumber> byProductId = productOrderUserNumberRepository.findByProductId(productId);
        if (byProductId.size() != 0) { // 결과가 있다면
            return byProductId.get(0).getUserNumber();
        }
        return -1L;
    }

    @Transactional
    public void plusCouponUseUserNumber(String couponType, long number) { // +1, -1
        List<CouponUseUserNumber> byProductId = couponUseUserNumberRepository.findByCouponType(couponType);

        if(byProductId.size() != 0){ // 결과가 있다면 숫자 바꾸기
            CouponUseUserNumber couponUseUserNumber = byProductId.get(0); // 객체 가져오기 & 연산
            couponUseUserNumber.changeUserNumber(couponUseUserNumber.getUserNumber()+number);

        }else{ // 결과가 없다면: 객체 생성해서 저장
            CouponUseUserNumber couponUseUserNumber = CouponUseUserNumber.builder()
                    .couponType(couponType)
                    .userNumber(number)
                    .build();
            couponUseUserNumberRepository.save(couponUseUserNumber);
        }

        // TODO 소켓-브로드캐스팅 결과 전달

    }

    public long getCouponUseUserNumber(String couponType) {
        List<CouponUseUserNumber> byProductId = couponUseUserNumberRepository.findByCouponType(couponType);
        if (byProductId.size() != 0) { // 결과가 있다면
            return byProductId.get(0).getUserNumber();
        }
        return -1L;
    }


}
