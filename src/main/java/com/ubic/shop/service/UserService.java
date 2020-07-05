package com.ubic.shop.service;

import com.ubic.shop.domain.User;
import com.ubic.shop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public User findOne(Long userId) {
        return userRepository.findById(userId).get();
    }
}
