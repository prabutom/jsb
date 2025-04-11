package com.example.logging.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "logging.framework")
public class LoggingProperties {
    private boolean enabled = true;
    private boolean logMethodEntryExit = true;
    private boolean logMethodParameters = true;
    private boolean logExecutionTime = true;
    private String serviceName;
    private String[] excludePackages = {};
}