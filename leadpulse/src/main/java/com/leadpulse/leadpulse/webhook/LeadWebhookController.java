package com.leadpulse.leadpulse.webhook;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
public class LeadWebhookController {

    @PostMapping("/api/v1/webhooks/zoho/leads")
    public ResponseEntity<String> receiveLead(@RequestBody Map<String,Object> payload,
                                              @RequestHeader(value = "X-Webhook-Secret",required = false) String webhookSecret){
        log.info("Received webhook. Secret header present: {}",webhookSecret != null);
        log.info("payload: {}",payload);

        return ResponseEntity.ok("Received");

    }
}
