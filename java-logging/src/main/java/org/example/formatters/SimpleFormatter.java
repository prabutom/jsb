package org.example.formatters;

import org.example.core.LogLevel;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SimpleFormatter implements LogFormatter {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public String format(String loggerName, LogLevel level, String message, Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        sb.append(DATE_FORMAT.format(new Date()));
        sb.append(" [").append(level).append("] ");
        sb.append("[").append(loggerName).append("] - ");
        sb.append(message);

        if (throwable != null) {
            sb.append("\n").append(throwableToString(throwable));
        }

        return sb.toString();
    }

    private String throwableToString(Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        sb.append(throwable.getClass().getName()).append(": ").append(throwable.getMessage()).append("\n");
        for (StackTraceElement element : throwable.getStackTrace()) {
            sb.append("\tat ").append(element.toString()).append("\n");
        }
        return sb.toString();
    }
}
