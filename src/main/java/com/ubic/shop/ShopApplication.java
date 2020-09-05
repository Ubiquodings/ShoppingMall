package com.ubic.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.session.SessionRepository;
import org.springframework.session.jdbc.JdbcIndexedSessionRepository;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestTemplate;

@EnableAsync
@EnableJpaRepositories(basePackages = "com.ubic.shop.repository")
@EnableElasticsearchRepositories(basePackages = "com.ubic.shop.elasticsearch")
@EnableJpaAuditing
@SpringBootApplication
public class ShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public SessionRepository jdbcIndexedSessionRepository(JdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate) {
        return new JdbcIndexedSessionRepository(jdbcTemplate, transactionTemplate);
    }
}
