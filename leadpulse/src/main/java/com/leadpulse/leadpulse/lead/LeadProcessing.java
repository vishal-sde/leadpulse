package com.leadpulse.leadpulse.lead;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "lead_processing")
public class LeadProcessing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lead_id",nullable = false)
    private String leadId;

    @Column(name = "event_id")
    private String eventId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status",nullable = false)
    private ProcessingStatus status;

    @Column(name = "ai_score")
    private Integer aiScore;

    @Column(name = "priority")
    private String priority;

    @Column(name = "territory")
    private String territory;

    @Column(name = "assigned_rep")
    private String assignedRep;

    @Column(name = "processing_started_at")
    private LocalDateTime processingStartedAt;

    @Column(name = "processing_completed_at")
    private LocalDateTime processingCompletedAt;

    @Column(name = "error_message",columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "created_at",nullable = false,updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate(){
        this.createdAt = LocalDateTime.now();
    }
}
