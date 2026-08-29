package com.leadpulse.leadpulse.lead;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeadProcessingService {

    private final LeadProcessingRepository leadProcessingRepository;

    @Async("leadProcessingExecutor")
    public void processLeadAsync(Long recordId, String leadid, Map<String,Object> payload){
        log.info("[{}] Starting async processing for lead {}",Thread.currentThread().getName(),leadid);


        LeadProcessing record = leadProcessingRepository.findById(recordId)
                .orElseThrow(() -> new IllegalStateException("Audit record not found: " + recordId));

        try{
            record.setStatus(ProcessingStatus.PROCESSING);
            leadProcessingRepository.save(record);

            //todo: enrichment and Ai scoring
            //todo: assignment

            record.setStatus(ProcessingStatus.COMPLETED);
            record.setProcessingCompletedAt(LocalDateTime.now());
            leadProcessingRepository.save(record);

            log.info("[{}] Completed processing for lead {}",Thread.currentThread().getName(),leadid);
        }catch (Exception e){
            log.error("[{}] Processing failed for lead {}: {}",Thread.currentThread().getName(),leadid,e.getMessage());
            record.setStatus(ProcessingStatus.FAILED);
            record.setErrorMessage(e.getMessage());
            leadProcessingRepository.save(record);
        }

    }
}
