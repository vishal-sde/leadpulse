package com.leadpulse.leadpulse.ai;

public record LeadScoreResult(
    int score,
    String priority,
    String reason,
    String recommondedAction
){}
