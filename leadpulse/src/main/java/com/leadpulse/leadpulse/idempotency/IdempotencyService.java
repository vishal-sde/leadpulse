package com.leadpulse.leadpulse.idempotency;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final StringRedisTemplate redisTemplate;

    private static final Duration LOCK_TTL = Duration.ofMinutes(10);

    /**
     * Attempts to claim this event as "being processed."
     * Returns true if this call successfully claimed it (i.e., it's new).
     * Returns false if it was already claimed (i.e., it's a duplicate).
     */

    public boolean tryClaim(String leadId,String eventType){
        String key = buildKey(leadId,eventType);

        Boolean wasSet = redisTemplate.opsForValue()
                .setIfAbsent(key,"PROCESSING",LOCK_TTL);

        boolean claimed = Boolean.TRUE.equals(wasSet);

        if(claimed) {
            log.info("Claimed idempotency key: {}", key);
        }else{
            log.info("Duplicate event detected, already claimed: {}",key);
        }

        return claimed;

    }

  private String buildKey(String leadId,String eventType){
        return "zoho:" + leadId + ":" + eventType;
  }
}
