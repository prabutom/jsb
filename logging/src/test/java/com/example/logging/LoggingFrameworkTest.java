package com.example.logging;

import com.example.logging.annotation.Loggable;
import com.example.logging.model.LogDetails;
import com.example.logging.service.LoggingService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@Import(TestConfig.class)
class LoggingFrameworkTest {

    @Autowired
    private TestService testService;

    @Autowired
    private LoggingService loggingService;

    @Test
    void testLoggingAspect() throws Exception {
        //Reset mock to clear previous interactions
     //   Mockito.reset(loggingService);
        testService.loggedMethod("test",123);
        verify(loggingService,atLeastOnce()).log(any(LogDetails.class));
        //verify(loggingService).log(any(LogDetails.class));
        //Verify the log method was called with any LogDetails
     //   verify(loggingService, timeout(1000)).log(any(LogDetails.class));
    }

/*    @Test
    void testErrorLogging() throws Exception {

        try {

            testService.errorMethod();
        } catch (RuntimeException e) {
// Expected
        }
     //   verify(loggingService, times(1)).log(any(LogDetails.class));
        verify(loggingService,timeout(1000)).log(argThat(log -> "ERROR".equals(log.getLevel())));
    }*/
@Test
void testErrorLogging() throws Exception {

    try {

        testService.errorMethod();
    } catch (RuntimeException e) {
// Expected
    }
    //   verify(loggingService, times(1)).log(any(LogDetails.class));
    //verify(loggingService,timeout(1000)).log(argThat(log -> "ERROR".equals(log.getLevel())));
    verify(loggingService,atLeastOnce()).log(argThat(log -> "ERROR".equals(log.getLevel())));
}

    // Test service class
    static class TestService {
        @Loggable
        public String loggedMethod(String param1, int param2) {
            return "result";
        }

        @Loggable
        public void errorMethod() {
            throw new RuntimeException("Test exception");
        }
    }

    @Test
    void debugLogginCalls()
    {
        testService.loggedMethod("test",123);
        Mockito.mockingDetails(loggingService).getInvocations().forEach(invocation -> System.out.println("Called: "+ invocation));

    }
}