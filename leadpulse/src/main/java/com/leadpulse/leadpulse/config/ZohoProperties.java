package com.leadpulse.leadpulse.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "zoho")
@Getter
@Setter
public class ZohoProperties {
    private String client_id;
    private String clientSecret;
    private String refreshToken;
    private String accountsUrl;
    private String apiDomain;



}
