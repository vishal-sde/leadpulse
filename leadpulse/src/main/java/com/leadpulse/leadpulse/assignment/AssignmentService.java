package com.leadpulse.leadpulse.assignment;


import com.leadpulse.leadpulse.config.AssignmentProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssignmentService {
    private final StringRedisTemplate stringRedisTemplate;
    private final AssignmentProperties assignmentProperties;

    private static final String COUNTER_KEY = "leadpulse:assignment:counter";

    public String assignRep(){
        List<String> reps = assignmentProperties.getReps();

        if(reps == null || reps.isEmpty()){
            throw new IllegalStateException("No sales reps configured for assignment");

        }

        Long counterValue = stringRedisTemplate.opsForValue().increment(COUNTER_KEY);

        int index = (int)((counterValue -1) % reps.size());
        String assignedRep = reps.get(index);

        log.info("Assignment counter={}, assigned to rep={}", counterValue,assignedRep);

        return assignedRep;
    }
}
