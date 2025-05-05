package org.example.handlers;

import org.example.core.LogLevel;
import org.example.model.LogEntry;
import org.example.formatters.LogFormatter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class RemoteHttpHandler implements LogHandler {
    private final LogFormatter formatter;
    private final String endpointUrl;
    private final HttpClient httpClient;
    private final String authToken;

    public RemoteHttpHandler(LogFormatter formatter, String endpointUrl, String authToken) {
        this.formatter = formatter;
        this.endpointUrl = endpointUrl;
        this.authToken = authToken;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    @Override
    public void handle(String loggerName, LogLevel level, String message, Throwable throwable) {
        String formattedMessage = formatter.format(loggerName, level, message, throwable);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpointUrl))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authToken)
                .POST(HttpRequest.BodyPublishers.ofString(formattedMessage))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                System.err.println("Remote logging failed with status: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Failed to send log to remote server: " + e.getMessage());
        }
    }
}