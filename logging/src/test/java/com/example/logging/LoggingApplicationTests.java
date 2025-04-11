package com.example.logging;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import com.example.logging.annotation.Loggable;
import com.example.logging.model.LogDetails;
import com.example.logging.service.LoggingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Import(TestConfig.class)
class LoggingApplicationTests {

	@Autowired
	private TestService testService;

	@Autowired
	private LoggingService loggingService;

	@Test
	void testLoggingAspect() throws Exception {
		testService.loggedMethod("test", 123);
		verify(loggingService, times(2)).log(any(LogDetails.class));
	}

	@Test
	void testErrorLogging() throws Exception {
		try {
			testService.errorMethod();
		} catch (RuntimeException e) {
// Expected
		}
		verify(loggingService, times(1)).log(any(LogDetails.class));
	}
}

class TestService {
	@Loggable
	public String loggedMethod(String param1, int param2) {
		return "result";
	}

	@Loggable
	public void errorMethod() {
		throw new RuntimeException("Test exception");
	}
}
