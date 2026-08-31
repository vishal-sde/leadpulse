package com.leadpulse.leadpulse.zoho;

import com.leadpulse.leadpulse.config.ZohoProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ZohoLeadUpdateService {

    private final ZohoAuthService zohoAuthService;
    private final ZohoProperties zohoProperties;
    private final WebClient webClient = WebClient.builder().build();

    public  void updateLeadResults(String leadId,Integer aiScore, String priority,String assignedRep){
        String accessToken = zohoAuthService.getAccessToken();

        Map<String,Object> fields = Map.of(
                "AI_Score",aiScore,
                "Priority",priority,
                "Assigned_Rep",assignedRep
        );

        Map<String,Object> requestBody = Map.of(
                "data", List.of(fields)
        );

    try {
        String response = webClient.put()
                .uri(zohoProperties.getApiDomain() + "/crm/v8/Leads/" + leadId)
                .header("Authorization", "Zoho-oauthtoken " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        log.info("Zoho update response for lead {}: {}", leadId, response);
    }catch (WebClientResponseException e){
        log.error("Zoho update FAILED for lead {}. Status: {}. Body: {}",leadId,e.getStatusCode(),e.getResponseBodyAsString());
        throw e;
    }
    }
}
