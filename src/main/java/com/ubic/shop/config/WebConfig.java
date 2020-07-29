package com.ubic.shop.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpSession;
import java.util.List;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class WebConfig implements WebMvcConfigurer {
    private final LoginUserArgumentResolver loginUserArgumentResolver;
//    private final HttpSession httpSession;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
//        log.info("\n addArgumentResolvers1 session id : "+httpSession.getId());
        argumentResolvers.add(loginUserArgumentResolver);
//        log.info("\n addArgumentResolvers2 session id : "+httpSession.getId());
    }
}
