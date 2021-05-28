package com.ubic.shop.service;

import com.ubic.shop.domain.user_number.AllViewUserNumber;
import com.ubic.shop.domain.user_number.ProductViewUserNumber;
import com.ubic.shop.dto.UpdateUserNumberDto;
import com.ubic.shop.repository.user_number.AllViewUserNumberRepository;
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

    private final ProductViewUserNumberRepository productViewUserNumberRepository;
    private final AllViewUserNumberRepository allViewUserNumberRepository;


    @Transactional
    public void plusProductViewUserNumber(long productId, long number) { // +1, -1
        List<ProductViewUserNumber> byProductId = productViewUserNumberRepository.findByProductId(productId);
        ProductViewUserNumber productViewUserNumber;
        if (byProductId.size() != 0) { // 결과가 있다면 숫자 바꾸기
            productViewUserNumber = byProductId.get(0); // 객체 가져오기 & 연산
            productViewUserNumber.changeUserNumber(productViewUserNumber.getUserNumber() + number);

        } else { // 결과가 없다면: 객체 생성해서 저장
            productViewUserNumber = ProductViewUserNumber.builder()
                    .productId(productId)
                    .userNumber(number)
                    .build();
            productViewUserNumberRepository.save(productViewUserNumber);
        }
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

}
