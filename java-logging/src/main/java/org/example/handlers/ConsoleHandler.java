package org.example.handlers;

import org.example.core.LogLevel;
import org.example.formatters.LogFormatter;

public class ConsoleHandler implements LogHandler {
    private final LogFormatter formatter;

    public ConsoleHandler(LogFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public void handle(String loggerName, LogLevel level, String message, Throwable throwable) {
        String formattedMessage = formatter.format(loggerName, level, message, throwable);
        if (level == LogLevel.ERROR || level == LogLevel.FATAL) {
            System.err.println(formattedMessage);
        } else {
            System.out.println(formattedMessage);
        }
    }
}