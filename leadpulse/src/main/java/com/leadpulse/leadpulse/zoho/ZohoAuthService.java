package com.leadpulse.leadpulse.zoho;

import com.leadpulse.leadpulse.config.ZohoProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
@RequiredArgsConstructor
public class ZohoAuthService {

    private final ZohoProperties zohoProperties;
    private final WebClient webClient = WebClient.builder().build();

    private volatile String cachedAccessToken;
    private volatile Instant tokenExpiresAt = Instant.EPOCH;
    private final ReentrantLock refreshLock = new ReentrantLock();

    public String getAccessToken(){
        //if we have a valid cached token,use it with 60s saftey buffer
        if(cachedAccessToken != null && Instant.now().isBefore(tokenExpiresAt.minusSeconds(60))){
            return cachedAccessToken;
        }

        //Lock to prevent multiple threads refershing simultanesouly
        refreshLock.lock();
        try{
            //Double-check after acquiring lock -another thread may have justrefershed
            if(cachedAccessToken != null && Instant.now().isBefore(tokenExpiresAt.minusSeconds(60))){
                return cachedAccessToken;
            }
            return refreshAccessToken();
        }finally {
            refreshLock.unlock();
        }
    }

    private String refreshAccessToken() {
        log.info("Refreshing Zoho access token");

        String rawResponse = webClient.post()
                .uri(zohoProperties.getAccountsUrl() + "/oauth/v2/token" +
                        "?grant_type=refresh_token" +
                        "&client_id=" + zohoProperties.getClient_id() +
                        "&client_secret=" + zohoProperties.getClientSecret() +
                        "&refresh_token=" + zohoProperties.getRefreshToken())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        log.info("Raw Zoho token response: {}", rawResponse);

        ObjectMapper mapper = new ObjectMapper();
        ZohoTokenResponse response;
        try {
            response = mapper.readValue(rawResponse, ZohoTokenResponse.class);
        } catch (Exception e) {
            throw new IllegalStateException("Could not parse Zoho token response: " + rawResponse, e);
        }

        if (response.getAccess_token() == null) {
            throw new IllegalStateException("Zoho did not return an access token. Raw response: " + rawResponse);
        }

        cachedAccessToken = response.getAccess_token();
        tokenExpiresAt = Instant.now().plusSeconds(response.getExpires_in());

        log.info("Zoho access token refreshed, expires in {} seconds", response.getExpires_in());
        return cachedAccessToken;
    }

}
