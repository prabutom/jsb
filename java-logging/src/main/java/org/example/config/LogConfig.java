package org.example.config;

import org.example.core.LogLevel;
import org.example.handlers.LogHandler;
import org.example.formatters.LogFormatter;
import org.example.formatters.JsonFormatter;
import org.example.formatters.SimpleFormatter;
import org.example.handlers.ConsoleHandler;
import org.example.handlers.FileHandler;

import java.util.ArrayList;
import java.util.List;

public class LogConfig {
    private LogLevel level;
    private final List<LogHandler> handlers;

    public LogConfig(LogLevel level, List<LogHandler> handlers) {
        this.level = level;
        this.handlers = handlers;
    }

    public LogLevel getLevel() {
        return level;
    }

    public List<LogHandler> getHandlers() {
        return handlers;
    }

    public void setLevel(LogLevel level) {
        this.level = level;
    }

    public static LogConfig getDefaultConfig() {
        List<LogHandler> handlers = new ArrayList<>();
        handlers.add(new ConsoleHandler(new SimpleFormatter()));
        return new LogConfig(LogLevel.INFO, handlers);
    }
}
