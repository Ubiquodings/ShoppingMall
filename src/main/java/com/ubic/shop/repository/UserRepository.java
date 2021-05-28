package com.ubic.shop.repository;

import com.ubic.shop.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByEmail(String email);

    User findByName(String name);

    @Query(value = "select u.id from User u where u.lastActivatedDate < :now and :before30m <= u.lastActivatedDate")
    List<Long> findUserIdByConTimeArrange(@Param(value="now") LocalDateTime now, @Param(value="before30m") LocalDateTime before30m);
}
