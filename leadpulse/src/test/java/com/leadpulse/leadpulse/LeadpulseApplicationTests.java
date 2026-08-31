package com.leadpulse.leadpulse;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled("Requires full app context with real datasource - not configured for unit test runs")
@SpringBootTest
class LeadpulseApplicationTests {

	@Test
	void contextLoads() {
	}

}
