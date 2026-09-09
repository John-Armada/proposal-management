package com.pointwest.prop.email.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableScheduling
@EnableConfigurationProperties(EmailProperties.class)
public class EmailConfig {

    @Bean
    public ObjectMapper emailObjectMapper() {
        return new ObjectMapper();
    }
}