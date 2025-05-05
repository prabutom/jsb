package org.example.core;

import org.example.handlers.LogHandler;
import org.example.config.LogConfig;

import java.util.List;

class StandardLogger implements Logger {
    private final String name;
    private LogLevel level;
    private final List<LogHandler> handlers;

    public StandardLogger(String name, LogConfig config) {
        this.name = name;
        this.level = config.getLevel();
        this.handlers = config.getHandlers();
    }

    @Override
    public void trace(String message) {
        log(LogLevel.TRACE, message, null);
    }

    @Override
    public void debug(String message) {
        log(LogLevel.DEBUG, message, null);
    }

    @Override
    public void info(String message) {
        log(LogLevel.INFO, message, null);
    }

    @Override
    public void warn(String message) {
        log(LogLevel.WARN, message, null);
    }

    @Override
    public void error(String message) {
        log(LogLevel.ERROR, message, null);
    }

    @Override
    public void fatal(String message) {
        log(LogLevel.FATAL, message, null);
    }

    @Override
    public void trace(String message, Throwable throwable) {
        log(LogLevel.TRACE, message, throwable);
    }

    @Override
    public void debug(String message, Throwable throwable) {
        log(LogLevel.DEBUG, message, throwable);
    }

    @Override
    public void info(String message, Throwable throwable) {
        log(LogLevel.INFO, message, throwable);
    }

    @Override
    public void warn(String message, Throwable throwable) {
        log(LogLevel.WARN, message, throwable);
    }

    @Override
    public void error(String message, Throwable throwable) {
        log(LogLevel.ERROR, message, throwable);
    }

    @Override
    public void fatal(String message, Throwable throwable) {
        log(LogLevel.FATAL, message, throwable);
    }

    private void log(LogLevel level, String message, Throwable throwable) {
        if (this.level.ordinal() <= level.ordinal()) {
            for (LogHandler handler : handlers) {
                handler.handle(name, level, message, throwable);
            }
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public LogLevel getLevel() {
        return level;
    }

    @Override
    public void setLevel(LogLevel level) {
        this.level = level;
    }
}
