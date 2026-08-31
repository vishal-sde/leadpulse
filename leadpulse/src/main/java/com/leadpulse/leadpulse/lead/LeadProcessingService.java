package com.leadpulse.leadpulse.lead;


import com.leadpulse.leadpulse.ai.LeadScoreResult;
import com.leadpulse.leadpulse.ai.LeadScoringService;
import com.leadpulse.leadpulse.assignment.AssignmentService;
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
    private final LeadScoringService leadScoringService;
    private final AssignmentService assignmentService;

    @Async("leadProcessingExecutor")
    public void processLeadAsync(Long recordId, String leadid, Map<String,Object> payload){
        log.info("[{}] Starting async processing for lead {}",Thread.currentThread().getName(),leadid);


        LeadProcessing record = leadProcessingRepository.findById(recordId)
                .orElseThrow(() -> new IllegalStateException("Audit record not found: " + recordId));

        try{
            record.setStatus(ProcessingStatus.PROCESSING);
            leadProcessingRepository.save(record);

            String company = String.valueOf(payload.get("Company"));
            String fullName = String.valueOf(payload.get("Full_Name"));
            String email = String.valueOf(payload.get("Email"));
            String leadScore = String.valueOf(payload.get("Lead_Source"));

            LeadScoreResult scoreResult = leadScoringService.scoreLead(company,fullName,email,leadScore);
            record.setAiScore(scoreResult.score());
            record.setPriority(scoreResult.priority());
            record.setStatus(ProcessingStatus.SCORED);
            leadProcessingRepository.save(record);

            log.info("[{}] Scored lead {}: {} ({})",Thread.currentThread().getName(),leadid,scoreResult.score(),scoreResult.priority());

           String assignedRep = assignmentService.assignRep();
           record.setAssignedRep(assignedRep);
           record.setStatus(ProcessingStatus.ASSIGNED);
           leadProcessingRepository.save(record);

           log.info("[{}] Assigned lead {} to {}",Thread.currentThread().getName(),leadid,assignedRep);

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
