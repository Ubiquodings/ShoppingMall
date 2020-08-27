package com.ubic.shop.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ubic.orders")
@Data
public class UbicConfig {
    public int productListPageSize = 100; // static 으로 해도 잘 동작하나 ? 안되는듯?
    public int productDetailPageSize = 20;
    public String baseUrl = "http://localhost:8080";

//    @Value("${spring.profile.value}")
    private String djangoServerUrl;
}
