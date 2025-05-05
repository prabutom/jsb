package org.example.formatters;

import org.example.core.LogLevel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class JsonFormatter implements LogFormatter {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    @Override
    public String format(String loggerName, LogLevel level, String message, Throwable throwable) {
        Map<String, Object> logEntry = new LinkedHashMap<>();
        logEntry.put("timestamp", DATE_FORMAT.format(new Date()));
        logEntry.put("level", level.name());
        logEntry.put("logger", loggerName);
        logEntry.put("message", message);

        if (throwable != null) {
            Map<String, Object> exceptionMap = new LinkedHashMap<>();
            exceptionMap.put("exceptionClass", throwable.getClass().getName());
            exceptionMap.put("message", throwable.getMessage());
            exceptionMap.put("stackTrace", getStackTrace(throwable));
            logEntry.put("exception", exceptionMap);
        }

        return toJsonString(logEntry);
    }

    private String[] getStackTrace(Throwable throwable) {
        StackTraceElement[] elements = throwable.getStackTrace();
        String[] stackTrace = new String[elements.length];
        for (int i = 0; i < elements.length; i++) {
            stackTrace[i] = elements[i].toString();
        }
        return stackTrace;
    }

    private String toJsonString(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("\"").append(entry.getKey()).append("\": ");
            Object value = entry.getValue();
            if (value instanceof String) {
                sb.append("\"").append(escapeJson((String) value)).append("\"");
            } else if (value instanceof Object[]) {
                sb.append("[");
                Object[] array = (Object[]) value;
                for (int i = 0; i < array.length; i++) {
                    if (i > 0) sb.append(", ");
                    sb.append("\"").append(escapeJson(array[i].toString())).append("\"");
                }
                sb.append("]");
            } else {
                sb.append("\"").append(value).append("\"");
            }
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }

    private String escapeJson(String input) {
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
