package org.example.formatters;

import org.example.core.LogLevel;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CsvFormatter implements LogFormatter {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private static final String CSV_HEADER = "Timestamp,Logger,Level,Message,Exception\n";

    @Override
    public String format(String loggerName, LogLevel level, String message, Throwable throwable) {
        StringBuilder csv = new StringBuilder();

        // Escape CSV special characters
        String escapedMessage = message.replace("\"", "\"\"");
        String escapedLogger = loggerName.replace("\"", "\"\"");

        csv.append("\"")
                .append(DATE_FORMAT.format(new Date())).append("\",\"")
                .append(escapedLogger).append("\",\"")
                .append(level.name()).append("\",\"")
                .append(escapedMessage).append("\",\"");

        if (throwable != null) {
            String exception = throwable.getClass().getName() + ": " +
                    throwable.getMessage().replace("\"", "\"\"");
            csv.append(exception);
        }

        csv.append("\"\n");
        return csv.toString();
    }
}
