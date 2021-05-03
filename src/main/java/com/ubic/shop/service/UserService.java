package com.ubic.shop.service;

import com.ubic.shop.domain.User;
import com.ubic.shop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public User findOne(Long userId) {
        return userRepository.findById(userId).get();
    }

    public Long updateLastActivatedTime(long userId) {
        Optional<User> byId = userRepository.findById(userId);
        if(byId.isPresent()){
            User user = byId.get();
            user.updateLastActivatedDate();
            return user.getId();
        }
        return -1L;
    }
}
