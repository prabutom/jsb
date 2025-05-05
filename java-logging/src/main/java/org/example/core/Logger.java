package org.example.core;

public interface Logger {
    void trace(String message);
    void debug(String message);
    void info(String message);
    void warn(String message);
    void error(String message);
    void fatal(String message);

    void trace(String message, Throwable throwable);
    void debug(String message, Throwable throwable);
    void info(String message, Throwable throwable);
    void warn(String message, Throwable throwable);
    void error(String message, Throwable throwable);
    void fatal(String message, Throwable throwable);

    String getName();
    LogLevel getLevel();
    void setLevel(LogLevel level);
}