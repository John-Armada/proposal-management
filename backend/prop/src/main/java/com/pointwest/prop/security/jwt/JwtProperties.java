package com.pointwest.prop.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "prop.jwt")
public class JwtProperties {

    private String secret;
    private String issuer = "prop";
    private long accessTokenTtlMinutes = 60 * 8;
}