package org.example.handlers;

import org.example.core.LogLevel;

public interface LogHandler {
    void handle(String loggerName, LogLevel level, String message, Throwable throwable);
}
