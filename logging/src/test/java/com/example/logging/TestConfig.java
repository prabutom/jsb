package com.example.logging;

import com.example.logging.aspect.LoggingAspect;
import com.example.logging.service.LoggingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import static org.mockito.Mockito.when;

@TestConfiguration
@EnableAspectJAutoProxy
public class TestConfig {

    @Bean
    public LoggingAspect loggingAspect(LoggingService loggingService){
        return new LoggingAspect(loggingService);
    }
    @Bean
    public LoggingService loggingService() {

        LoggingService mock = Mockito.mock(LoggingService.class);
        Mockito.when(mock.isLoggingEnabled()).thenReturn(true);
        //when(mock.isLoggingEnabled()).thenReturn(true);
         return mock;
     //   return Mockito.mock(LoggingService.class);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public LoggingFrameworkTest.TestService testService(){
        return new LoggingFrameworkTest.TestService();
    }
}