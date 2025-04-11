package com.example.logging.config;

import com.example.logging.aspect.LoggingAspect;
import com.example.logging.model.LoggingProperties;
import com.example.logging.service.LoggingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(LoggingProperties.class)
public class LoggingAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public LoggingService loggingService(LoggingProperties loggingProperties, ObjectMapper objectMapper) {
        return new LoggingService(loggingProperties, objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public LoggingAspect loggingAspect(LoggingService loggingService) {
        return new LoggingAspect(loggingService);
    }
}
