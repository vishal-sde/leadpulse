package com.leadpulse.leadpulse.assignment;


import com.leadpulse.leadpulse.config.AssignmentProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.lenient;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class AssignmentServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String,String> valueOperations;

    @Mock
    private AssignmentProperties assignmentProperties;

    @InjectMocks
    private AssignmentService assignmentService;

    @BeforeEach
    void setUp(){
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(assignmentProperties.getReps()).thenReturn(List.of("Abinanth","Arul","Rohith"));
    }

    @Test
    void assignsFirstRepWhenCounterIsOne(){
        when(valueOperations.increment("leadpulse:assignment:counter")).thenReturn(1L);

        String rep = assignmentService.assignRep();
        assertThat(rep).isEqualTo("Abinanth");
    }

    @Test
    void cyclesBackToFirstRepAfterFullRotation(){
        when(valueOperations.increment("leadpulse:assignment:counter")).thenReturn(4L);

        String rep = assignmentService.assignRep();

        assertThat(rep).isEqualTo("Abinanth");
    }

    @Test
    void assignThirdRepCorrectly(){
        when(valueOperations.increment("leadpulse:assignment:counter")).thenReturn(3L);

        String rep = assignmentService.assignRep();

        assertThat(rep).isEqualTo("Rohith");
    }

    @Test
    void throwsWhenNoRepsConfigured(){
        when(assignmentProperties.getReps()).thenReturn(List.of());

        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalStateException.class,
                () -> assignmentService.assignRep()
        );
    }
}
