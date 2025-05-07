package org.example.config;

import org.example.core.LogLevel;
import org.example.exceptions.LogConfigurationException;
import org.example.formatters.*;
import org.example.handlers.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.json.JSONObject;
import org.json.JSONTokener;

public class LogConfigLoader {
    private static final String DEFAULT_CONFIG_FILE = "log-config.json";

    public static LogConfig loadConfig() throws LogConfigurationException {
        return loadConfig(DEFAULT_CONFIG_FILE);
    }

    public static LogConfig loadConfig(String configPath) throws LogConfigurationException {
        try {
            JSONObject configJson = loadConfigJson(configPath);
            return parseConfig(configJson);
        } catch (IOException e) {
            throw new LogConfigurationException("Failed to load configuration file: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new LogConfigurationException("Invalid configuration: " + e.getMessage(), e);
        }
    }

    private static JSONObject loadConfigJson(String configPath) throws IOException {
        Path path = Paths.get(configPath);
        if (Files.exists(path)) {
            try (InputStream is = Files.newInputStream(path)) {
                return new JSONObject(new JSONTokener(is));
            }
        }

        // Try classpath resource
        try (InputStream is = LogConfigLoader.class.getClassLoader().getResourceAsStream(DEFAULT_CONFIG_FILE)) {
            if (is != null) {
                return new JSONObject(new JSONTokener(is));
            }
        }

        throw new IOException("Configuration file not found at " + configPath + " or in classpath");
    }

    private static LogConfig parseConfig(JSONObject configJson) {
        LogLevel level = LogLevel.valueOf(configJson.getString("level").toUpperCase());
        List<LogHandler> handlers = new ArrayList<>();

        if (configJson.has("handlers")) {
            for (Object handlerObj : configJson.getJSONArray("handlers")) {
                JSONObject handlerJson = (JSONObject) handlerObj;
                String type = handlerJson.getString("type");
                LogHandler handler = createHandler(type, handlerJson);
                if (handler != null) {
                    handlers.add(handler);
                }
            }
        }

        return new LogConfig(level, handlers);
    }

    // Add these methods to the existing LogConfigLoader class
    private static LogHandler createHandler(String type, JSONObject handlerJson) {
        switch (type.toLowerCase()) {
            case "console":
                String consoleFormat = handlerJson.optString("format", "simple");
                return new ConsoleHandler(createFormatter(consoleFormat, handlerJson));
            case "file":
                String filePath = handlerJson.getString("path");
                String fileFormat = handlerJson.optString("format", "simple");
                return new FileHandler(createFormatter(fileFormat, handlerJson), filePath);
            case "database":
                String jdbcUrl = handlerJson.getString("jdbcUrl");
                String tableName = handlerJson.optString("tableName", "application_logs");
                Properties props = new Properties();
                if (handlerJson.has("username")) {
                    props.setProperty("user", handlerJson.getString("username"));
                }
                if (handlerJson.has("password")) {
                    props.setProperty("password", handlerJson.getString("password"));
                }
                String dbFormat = handlerJson.optString("format", "json");
                return new DatabaseHandler(
                        createFormatter(dbFormat, handlerJson),
                        jdbcUrl,
                        props,
                        tableName
                );
            case "remote":
                String endpoint = handlerJson.getString("endpoint");
                String authToken = handlerJson.optString("authToken", "");
                String remoteFormat = handlerJson.optString("format", "json");
                return new RemoteHttpHandler(
                        createFormatter(remoteFormat, handlerJson),
                        endpoint,
                        authToken
                );
            default:
                throw new IllegalArgumentException("Unknown handler type: " + type);
        }
    }

    private static LogFormatter createFormatter(String format, JSONObject config) {
        switch (format.toLowerCase()) {
            case "json":
                return new JsonFormatter();
            case "simple":
                return new SimpleFormatter();
            case "xml":
                return new XmlFormatter();
            case "csv":
                return new CsvFormatter();
            default:
                throw new IllegalArgumentException("Unknown formatter: " + format);
        }
    }

}
