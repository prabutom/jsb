package org.example.handlers;

import org.example.core.LogLevel;
import org.example.model.LogEntry;
import org.example.formatters.LogFormatter;

import java.sql.*;
import java.util.Properties;

public class DatabaseHandler implements LogHandler {
    private final LogFormatter formatter;
    private final String jdbcUrl;
    private final Properties connectionProperties;
    private final String tableName;

    public DatabaseHandler(LogFormatter formatter, String jdbcUrl,
                           Properties connectionProperties, String tableName) {
        this.formatter = formatter;
        this.jdbcUrl = jdbcUrl;
        this.connectionProperties = connectionProperties;
        this.tableName = tableName;
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "timestamp TIMESTAMP," +
                "logger_name VARCHAR(255)," +
                "level VARCHAR(10)," +
                "message TEXT," +
                "exception TEXT" +
                ")";

        try (Connection conn = DriverManager.getConnection(jdbcUrl, connectionProperties);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create log table", e);
        }
    }

    @Override
    public void handle(String loggerName, LogLevel level, String message, Throwable throwable) {
        String formattedMessage = formatter.format(loggerName, level, message, throwable);
        String exceptionStackTrace = throwable != null ? getStackTrace(throwable) : null;

        String sql = "INSERT INTO " + tableName + " (timestamp, logger_name, level, message, exception) " +
                "VALUES (CURRENT_TIMESTAMP, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(jdbcUrl, connectionProperties);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, loggerName);
            pstmt.setString(2, level.name());
            pstmt.setString(3, message);
            pstmt.setString(4, exceptionStackTrace);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Failed to write log to database: " + e.getMessage());
        }
    }

    private String getStackTrace(Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        sb.append(throwable.getClass().getName()).append(": ").append(throwable.getMessage()).append("\n");
        for (StackTraceElement element : throwable.getStackTrace()) {
            sb.append("\tat ").append(element.toString()).append("\n");
        }
        return sb.toString();
    }
}
