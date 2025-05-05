package org.example.model;

import org.example.core.LogLevel;

import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;

public class LogEntry {
    private final Instant timestamp;
    private final String loggerName;
    private final LogLevel level;
    private final String message;
    private final Throwable throwable;
    private final String threadName;

    public LogEntry(String loggerName, LogLevel level, String message, Throwable throwable) {
        this.timestamp = Instant.now();
        this.loggerName = loggerName;
        this.level = level;
        this.message = message;
        this.throwable = throwable;
        this.threadName = Thread.currentThread().getName();
    }

    // Getters
    public Instant getTimestamp() { return timestamp; }
    public String getLoggerName() { return loggerName; }
    public LogLevel getLevel() { return level; }
    public String getMessage() { return message; }
    public Throwable getThrowable() { return throwable; }
    public String getThreadName() { return threadName; }

    @Override
    public String toString() {
        return "LogEntry{" +
                "timestamp=" + timestamp +
                ", loggerName='" + loggerName + '\'' +
                ", level=" + level +
                ", message='" + message + '\'' +
                ", throwable=" + (throwable != null ? throwable.getClass().getName() : "null") +
                ", threadName='" + threadName + '\'' +
                '}';
    }
}
