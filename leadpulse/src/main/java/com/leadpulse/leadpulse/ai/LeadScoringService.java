package com.leadpulse.leadpulse.ai;


import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class LeadScoringService {

    private final ChatClient chatClient;

    public LeadScoringService(ChatClient.Builder chatClientBuilder){
        this.chatClient = chatClientBuilder.build();
    }

    public LeadScoreResult scoreLead(String company,String fullName,String email,String leadScore){

        String prompt = """
                You are a sales lead qualification assistant for a B2B SaaS company.
                Score the following lead from 0-100 based on how likely they are to convert into a paying customer.
                
                Lead details:
                - Company: %s
                - Contact name: %s
                - Email: %s
                - Lead source: %s
                
                Consider: presence of a real company name, a business email domain (not gmail/yahoo/hotmail),
                and a legitimate-sounding lead source as positive signals. Missing or placeholder-looking data
                should lower the score.
                
                Respond with a score (0-100), a priority (LOW, MEDIUM, or HIGH), a one-sentence reason,
                and a recommended next action.
                """.formatted(
                        nullSafe(company),
                        nullSafe(fullName),
                        nullSafe(email),
                        nullSafe(leadScore)
        );

        log.info("Sending lead to AI scoring model");

        LeadScoreResult result = chatClient.prompt()
                .user(prompt)
                .call()
                .entity(LeadScoreResult.class);

                log.info("AI scoring result: score={}. priority={}",result.score(),result.priority());

                return result;
    }


    private String nullSafe(String value){
        return (value == null || value.isBlank()) ? "Not provided" : value;
    }

}
