package com.ubic.shop.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ubic.orders")
@Data
public class UbicConfig {
    public int productListPageSize = 100;
    public int productDetailPageSize = 20;
    public String baseUrl = "http://localhost:8080";
    public int dashBoardProductListPageSize=0;

    private String djangoServerUrl; // '/' 없다!
    private String localDjangoServerUrl;
}
