package com.example.logging.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LogDetails {
    private Instant timestamp;
    private String level;
    private String message;
    private String serviceName;
    private String className;
    private String methodName;
    private Map<String, Object> parameters;
    private String exception;
    private String stackTrace;
    private Map<String, String> context;
}