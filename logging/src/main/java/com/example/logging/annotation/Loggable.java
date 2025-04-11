package com.example.logging.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Loggable {
    boolean logParameters() default true;
    boolean logExecutionTime() default true;
    boolean logResult() default false;
    String level() default "INFO";
}
