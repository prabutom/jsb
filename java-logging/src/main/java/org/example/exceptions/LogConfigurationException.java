package org.example.exceptions;

public class LogConfigurationException extends Exception {
    public LogConfigurationException(String message) {
        super(message);
    }

    public LogConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
