package org.example.handlers;

import org.example.core.LogLevel;
import org.example.formatters.LogFormatter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileHandler implements LogHandler {
    private final LogFormatter formatter;
    private final Path filePath;

    public FileHandler(LogFormatter formatter, String filePath) {
        this.formatter = formatter;
        this.filePath = Paths.get(filePath);
        ensureFileExists();
    }

    private void ensureFileExists() {
        try {
            if (!Files.exists(filePath)) {
                Files.createDirectories(filePath.getParent());
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create log file", e);
        }
    }

    @Override
    public void handle(String loggerName, LogLevel level, String message, Throwable throwable) {
        String formattedMessage = formatter.format(loggerName, level, message, throwable);
        try (BufferedWriter writer = Files.newBufferedWriter(filePath,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(formattedMessage);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }
}