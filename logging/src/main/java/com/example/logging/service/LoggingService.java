package com.example.logging.service;

import com.example.logging.model.LogDetails;
import com.example.logging.model.LoggingProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoggingService {
    private final LoggingProperties loggingProperties;
    private final ObjectMapper objectMapper;

    public boolean isLoggingEnabled() {
        return loggingProperties.isEnabled();
    }

    public void log(LogDetails logDetails) {
        if (!isLoggingEnabled()) {
            return;
        }

// Set common fields
        logDetails.setTimestamp(Instant.now());
        logDetails.setServiceName(loggingProperties.getServiceName());

        try {
            String logMessage = objectMapper.writeValueAsString(logDetails);

            switch (logDetails.getLevel().toUpperCase()) {
                case "ERROR":
                    log.error(logMessage);
                    break;
                case "WARN":
                    log.warn(logMessage);
                    break;
                case "DEBUG":
                    log.debug(logMessage);
                    break;
                case "TRACE":
                    log.trace(logMessage);
                    break;
                default: // INFO
                    log.info(logMessage);
            }
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize log details: {}", e.getMessage());
        }
    }

    public void logError(String message, Throwable throwable, String className, String methodName) {
        log(LogDetails.builder()
                .level("ERROR")
                .message(message)
                .className(className)
                .methodName(methodName)
                .exception(throwable.getClass().getName())
                .stackTrace(getStackTraceAsString(throwable))
                .build());
    }

    public void logInfo(String message, String className, String methodName) {
        log(LogDetails.builder()
                .level("INFO")
                .message(message)
                .className(className)
                .methodName(methodName)
                .build());
    }

    private String getStackTraceAsString(Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : throwable.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }
}