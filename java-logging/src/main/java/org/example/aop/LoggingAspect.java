package org.example.aop;

import org.example.annotations.*;
import org.example.core.Logger;
import org.example.core.LoggerFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Aspect
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("@within(loggable) || @annotation(logMethod)")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint,
                                     Loggable loggable,
                                     LogMethod logMethod) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // Get effective logging configuration
        LogMethod effectiveLogMethod = getEffectiveLogMethod(method, logMethod, loggable);

        String methodName = method.getDeclaringClass().getSimpleName() + "." + method.getName();
        Logger methodLogger = LoggerFactory.getLogger(method.getDeclaringClass());

        try {
            // Log method entry
            if (shouldLogMethod(loggable, effectiveLogMethod)) {
                logMethodEntry(methodLogger, effectiveLogMethod, methodName,
                        effectiveLogMethod.logParams() ? joinPoint.getArgs() : null);
            }

            long startTime = System.nanoTime();
            Object result = joinPoint.proceed();
            long executionTime = System.nanoTime() - startTime;

            // Log method exit
            if (shouldLogMethod(loggable, effectiveLogMethod)) {
                logMethodExit(methodLogger, effectiveLogMethod, methodName,
                        effectiveLogMethod.logResult() ? result : null,
                        effectiveLogMethod.logExecutionTime() ? executionTime : -1);
            }

            return result;
        } catch (Throwable ex) {
            // Exception logging is handled by @LogException aspect
            throw ex;
        }
    }

    @Around("@annotation(logException)")
    public Object logException(ProceedingJoinPoint joinPoint, LogException logException) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (Throwable ex) {
            if (shouldLogException(ex, logException.forExceptions())) {
                Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
                Logger exceptionLogger = LoggerFactory.getLogger(method.getDeclaringClass());

                String message = logException.message().isEmpty() ?
                        "Exception in " + method.getName() : logException.message();

                if (logException.withStackTrace()) {
                    exceptionLogger.log(logException.value(), message, ex);
                } else {
                    exceptionLogger.log(logException.value(),
                            message + ": " + ex.getClass().getSimpleName() +
                                    " - " + ex.getMessage());
                }
            }
            throw ex;
        }
    }

    private boolean shouldLogException(Throwable ex, Class<? extends Throwable>[] forExceptions) {
        if (forExceptions.length == 0) return true;

        return Arrays.stream(forExceptions)
                .anyMatch(exceptionClass -> exceptionClass.isAssignableFrom(ex.getClass()));
    }

    private LogMethod getEffectiveLogMethod(Method method, LogMethod methodAnnotation, Loggable classAnnotation) {
        if (methodAnnotation != null) return methodAnnotation;

        if (classAnnotation != null) {
            return new LogMethod() {
                public LogLevel value() { return classAnnotation.value(); }
                public String message() { return ""; }
                public boolean logParams() { return classAnnotation.logParams(); }
                public boolean logResult() { return classAnnotation.logResults(); }
                public boolean logExecutionTime() { return false; }
                public Class<? extends java.lang.annotation.Annotation> annotationType() { return LogMethod.class; }
            };
        }

        // Default values if neither annotation is present
        return new LogMethod() {
            public LogLevel value() { return LogLevel.DEBUG; }
            public String message() { return ""; }
            public boolean logParams() { return true; }
            public boolean logResult() { return true; }
            public boolean logExecutionTime() { return false; }
            public Class<? extends java.lang.annotation.Annotation> annotationType() { return LogMethod.class; }
        };
    }

    private boolean shouldLogMethod(Loggable loggable, LogMethod logMethod) {
        return loggable != null ? loggable.logMethod() : logMethod != null;
    }

    private void logMethodEntry(Logger logger, LogMethod logMethod, String methodName, Object[] params) {
        String message = logMethod.message().isEmpty() ?
                "Entering " + methodName : logMethod.message();

        if (params != null && params.length > 0) {
            message += " with params: " + Arrays.toString(params);
        }

        logger.log(logMethod.value(), message);
    }

    private void logMethodExit(Logger logger, LogMethod logMethod, String methodName, Object result, long executionTime) {
        StringBuilder message = new StringBuilder();
        message.append(logMethod.message().isEmpty() ?
                "Exiting " + methodName : logMethod.message());

        if (logMethod.logResult() && result != null) {
            message.append(", result: ").append(result);
        }

        if (executionTime >= 0) {
            message.append(", execution time: ")
                    .append(TimeUnit.NANOSECONDS.toMillis(executionTime))
                    .append("ms");
        }

        logger.log(logMethod.value(), message.toString());
    }
}
