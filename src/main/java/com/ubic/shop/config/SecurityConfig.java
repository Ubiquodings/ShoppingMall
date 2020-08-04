package com.ubic.shop.config;

import com.ubic.shop.domain.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .headers().frameOptions().disable()
                .and()
                .authorizeRequests()
                .antMatchers(/*일단 전체 public*/"/**", "/css/**", "/images/**", "/js/**", "/h2-console/**", "/profile", "/products/**", "/categories/**", "/excel/**", "/api/**",
                        /*비회원로직 포함*/"/mypage/**","/carts/**","/orders/**")
                        .permitAll()
                .antMatchers("/api/v1/**"/*,"/mypage/**","/carts/**","/orders/**"*/).hasRole(Role.GUEST.name())
                .anyRequest().authenticated()
                .and()
                .logout()
                .logoutSuccessUrl("/")
                .and()
                .oauth2Login()
                .userInfoEndpoint()
                .userService(customOAuth2UserService);
    }
}