package org.example.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogException {
    /**
     * Log level for exceptions
     */
    org.example.core.LogLevel value() default org.example.core.LogLevel.ERROR;

    /**
     * Exception types to log (empty = all exceptions)
     */
    Class<? extends Throwable>[] forExceptions() default {};

    /**
     * Whether to log stack trace
     */
    boolean withStackTrace() default true;

    /**
     * Custom message prefix
     */
    String message() default "Exception occurred";
}
