package com.leadpulse.leadpulse.webhook;

import com.leadpulse.leadpulse.idempotency.IdempotencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class LeadWebhookController {

    private final IdempotencyService idempotencyService;

    @PostMapping("/api/v1/webhooks/zoho/leads")
    public ResponseEntity<String> receiveLead(@RequestBody Map<String,Object> payload,
                                              @RequestHeader(value = "X-Webhook-Secret",required = false) String webhookSecret){

        String leadId = String.valueOf(payload.get("id"));

        boolean isNewEvent = idempotencyService.tryClaim(leadId,"create");

        if(!isNewEvent){
            log.info("Skipping duplicate webhook for lead {}",leadId);
            return ResponseEntity.ok("duplicate,already processed");
        }

        log.info("Processing new lead: {}",leadId);
        log.info("Payload: {}",payload);

        //todo: actual processing enrichment,AI scoring,assignment goeas here later

        return ResponseEntity.ok("Received");

    }
}
