package com.leadpulse.leadpulse.idempotency;


import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Disabled("Test Containers cannot connect to Docker Desktop on this Windows setup (npipe/TCP API mismatch) - works in CI/Linux environments")
@Testcontainers
@SpringBootTest
public class IdempotencyServiceIntegrationTest {

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    static void redisProperties(DynamicPropertyRegistry registry){
        registry.add("spring.data.redis.host",redis::getHost);
        registry.add("spring.data.redis.port",()->redis.getMappedPort(6379));
    }

    @Autowired
    private IdempotencyService idempotencyService;

    @Test
    void firstClaimSucceedsSecondClaimFails(){
        String leadId = "test-lead-123";

        boolean firstAttempt = idempotencyService.tryClaim(leadId,"create");
        boolean secondAttempt = idempotencyService.tryClaim(leadId,"create");

        assertThat(firstAttempt).isTrue();
        assertThat(secondAttempt).isFalse();

    }

    @Test
    void differentLeadIdsAreIndependent(){
        boolean lead1 = idempotencyService.tryClaim("lead-A","create");
        boolean lead2 = idempotencyService.tryClaim("lead-B","create");

        assertThat(lead1).isTrue();
        assertThat(lead2).isTrue();
    }
}
