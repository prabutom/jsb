package org.example.formatters;

import org.example.core.LogLevel;

public interface LogFormatter {
    String format(String loggerName, LogLevel level, String message, Throwable throwable);
}