package org.example.core;

import org.example.config.LogConfig;
import org.example.config.LogConfigLoader;
import org.example.exceptions.LogConfigurationException;

import java.util.HashMap;
import java.util.Map;

public class LoggerFactory {
    private static final Map<String, Logger> loggers = new HashMap<>();
    private static LogConfig config;

    static {
        try {
            config = LogConfigLoader.loadConfig();
        } catch (LogConfigurationException e) {
            System.err.println("Failed to load logging configuration: " + e.getMessage());
            config = LogConfig.getDefaultConfig();
        }
    }

    public static Logger getLogger(Class<?> clazz) {
        return getLogger(clazz.getName());
    }

    public static Logger getLogger(String name) {
        return loggers.computeIfAbsent(name, k -> new StandardLogger(name, config));
    }

    public static void setConfig(LogConfig newConfig) {
        config = newConfig;
        loggers.clear(); // Reset all loggers to use new config
    }
}