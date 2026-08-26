package com.leadpulse.leadpulse.zoho;

import com.leadpulse.leadpulse.config.ZohoProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequiredArgsConstructor
public class ZohoTestController {

    private final ZohoAuthService zohoAuthService;
    private final ZohoProperties zohoProperties;
    private final WebClient webClient = WebClient.builder().build();


    @GetMapping("/test/zoho-leads")
    public String testFetchLeads() {
        String accessToken = zohoAuthService.getAccessToken();

        try {
            String result = webClient.get()
                    .uri(zohoProperties.getApiDomain() + "/crm/v8/Leads?fields=Last_Name,Email,Company,Lead_Status&per_page=5")
                    .header("Authorization", "Zoho-oauthtoken " + accessToken)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return (result == null || result.isBlank())
                    ? "SUCCESS but empty response — likely no Leads exist in this CRM account."
                    : result;

        } catch (org.springframework.web.reactive.function.client.WebClientResponseException e) {
            return "ERROR " + e.getStatusCode() + ": " + e.getResponseBodyAsString();
        }
    }
}
