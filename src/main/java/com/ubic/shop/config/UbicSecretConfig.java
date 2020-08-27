package com.ubic.shop.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ubic.secret")
@Data
public class UbicSecretConfig {
    public String etriApiKey;
}
