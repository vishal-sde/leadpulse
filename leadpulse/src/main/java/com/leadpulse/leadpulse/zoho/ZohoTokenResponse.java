package com.leadpulse.leadpulse.zoho;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ZohoTokenResponse {
    private String access_token;
    private String api_domain;
    private String token_type;
    private int expires_in;
}
