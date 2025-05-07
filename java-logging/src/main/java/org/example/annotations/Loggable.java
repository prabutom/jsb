package org.example.annotations;

import org.example.core.LogLevel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Loggable {
    /**
     * Default log level for all methods in the class
     */
    LogLevel value() default LogLevel.INFO;

    /**
     * Whether to log method entry/exit
     */
    boolean logMethod() default true;

    /**
     * Whether to log method parameters
     */
    boolean logParams() default true;

    /**
     * Whether to log return values
     */
    boolean logResults() default true;
}