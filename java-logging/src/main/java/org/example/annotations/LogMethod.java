package org.example.annotations;

import org.example.core.LogLevel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogMethod {
    /**
     * Log level for this method
     */
    LogLevel value() default LogLevel.INFO;

    /**
     * Custom message prefix
     */
    String message() default "";

    /**
     * Whether to log method parameters
     */
    boolean logParams() default true;

    /**
     * Whether to log return value
     */
    boolean logResult() default true;

    /**
     * Whether to log execution time
     */
    boolean logExecutionTime() default false;
}