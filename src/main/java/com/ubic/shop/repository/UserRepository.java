package com.ubic.shop.repository;

import com.ubic.shop.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByEmail(String email);

    /*
    *   save
        findOne
        findAll
        findByName
    * */
}
