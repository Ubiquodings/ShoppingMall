package com.ubic.shop.domain;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.annotation.Id;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@NoArgsConstructor
@Entity
@Getter @Slf4j
@Setter(AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    private String name;
    private String email;
    private String picture;
    private Role role;

    private LocalDateTime lastActivatedDate = LocalDateTime.now();

    public User update(String name, String picture){
        this.name = name;
        this.picture = picture;
        return this;
    }

    public Long updateLastActivatedDate(){
        this.lastActivatedDate = LocalDateTime.now();
        LocalDateTime now = LocalDateTime.now();
//        now.minusMinutes(30);
//        log.info(""+now);

        return this.id;
    }

    public String getRoleKey(){
        return this.role.getKey();
    }

    @Builder
    public User(String name, String email, String picture, Role role) {
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.role = role;
    }
}
