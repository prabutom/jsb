package org.example.aop;

import org.aspectj.lang.annotation.Pointcut;
import org.example.annotations.*;
import org.example.core.Logger;
import org.example.core.LogLevel;
import org.example.core.LoggerFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.Class;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Aspect
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    // Handle class-level @Loggable
    @Around("@within(org.example.annotations.Loggable)")
    public Object logClassLevel(ProceedingJoinPoint joinPoint) throws Throwable {
        Loggable loggable = joinPoint.getTarget().getClass()
                .getAnnotation(Loggable.class);
        return logExecution(joinPoint, loggable, null);
    }

    // Handle method-level @LogMethod
    @Around("@annotation(org.example.annotations.LogMethod)")
    public Object logMethodLevel(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        LogMethod logMethod = signature.getMethod()
                .getAnnotation(LogMethod.class);
        return logExecution(joinPoint, null, logMethod);
    }

    // Common implementation
    private Object logExecution(ProceedingJoinPoint joinPoint,
                                Loggable loggable,
                                LogMethod logMethod) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        LogMethod effectiveLogMethod = getEffectiveLogMethod(method, logMethod, loggable);
        String methodName = method.getDeclaringClass().getSimpleName() + "." + method.getName();

        try {
            if (shouldLogMethod(loggable, effectiveLogMethod)) {
                logMethodEntry(logger, effectiveLogMethod, methodName,
                        effectiveLogMethod.logParams() ? joinPoint.getArgs() : null);
            }

            long startTime = System.nanoTime();
            Object result = joinPoint.proceed();
            long executionTime = System.nanoTime() - startTime;

            if (shouldLogMethod(loggable, effectiveLogMethod)) {
                logMethodExit(logger, effectiveLogMethod, methodName,
                        effectiveLogMethod.logResult() ? result : null,
                        effectiveLogMethod.logExecutionTime() ? executionTime : -1);
            }

            return result;
        } catch (Throwable ex) {
            // Exception handling would go here
            throw ex;
        }
    }
//    // Pointcut for class-level @Loggable
//    @Pointcut("@within(loggable)")
//    public void classLevelLogging(Loggable loggable) {}
//
//    // Pointcut for method-level @LogMethod
//    @Pointcut("@annotation(logMethod)")
//    public void methodLevelLogging(LogMethod logMethod) {}
//
//    // Combined pointcut
//    @Pointcut("classLevelLogging(loggable) || methodLevelLogging(logMethod)")
//    public void loggingPointcut(Loggable loggable, LogMethod logMethod) {}
//
//    // Advice using the combined pointcut
//    @Around("loggingPointcut(loggable, logMethod)")
//    public Object logMethodExecution(ProceedingJoinPoint joinPoint,
//                                     Loggable loggable,
//                                     LogMethod logMethod) throws Throwable {
//        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
//        Method method = signature.getMethod();
//
//        // Get effective logging configuration
//        LogMethod effectiveLogMethod = getEffectiveLogMethod(method, logMethod, loggable);
//
//        String methodName = method.getDeclaringClass().getSimpleName() + "." + method.getName();
//        Logger methodLogger = LoggerFactory.getLogger(method.getDeclaringClass());
//
//        try {
//            // Log method entry
//            if (shouldLogMethod(loggable, effectiveLogMethod)) {
//                logMethodEntry(methodLogger, effectiveLogMethod, methodName,
//                        effectiveLogMethod.logParams() ? joinPoint.getArgs() : null);
//            }
//
//            long startTime = System.nanoTime();
//            Object result = joinPoint.proceed();
//            long executionTime = System.nanoTime() - startTime;
//
//            // Log method exit
//            if (shouldLogMethod(loggable, effectiveLogMethod)) {
//                logMethodExit(methodLogger, effectiveLogMethod, methodName,
//                        effectiveLogMethod.logResult() ? result : null,
//                        effectiveLogMethod.logExecutionTime() ? executionTime : -1);
//            }
//
//            return result;
//        } catch (Throwable ex) {
//            // Exception logging is handled by @LogException aspect
//            throw ex;
//        }
//    }

    @Around("@annotation(logException)")
    public Object logException(ProceedingJoinPoint joinPoint, LogException logException) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (Throwable ex) {
            if (shouldLogException(ex, logException.forExceptions())) {
                Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
                String message = logException.message().isEmpty() ?
                        "Exception in " + method.getName() : logException.message();

                if (logException.withStackTrace()) logger.log(logException.value(), message, ex);
                else {
                    logger.log(logException.value(),
                            message + ": " + ex.getClass().getSimpleName() +
                                    " - " + ex.getMessage(),null);
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

//    private LogMethod getEffectiveLogMethod(Method method, LogMethod methodAnnotation, Loggable classAnnotation) {
//        if (methodAnnotation != null) return methodAnnotation;
//
//        if (classAnnotation != null) {
//            return new LogMethod() {
//                public LogLevel value() { return classAnnotation.value(); }
//                public String message() { return ""; }
//                public boolean logParams() { return classAnnotation.logParams(); }
//                public boolean logResult() { return classAnnotation.logResults(); }
//                public boolean logExecutionTime() { return false; }
//                public Class<? extends java.lang.annotation.Annotation> annotationType() { return LogMethod.class; }
//            };
//        }
//
//        // Default values if neither annotation is present
//        return new LogMethod() {
//            public LogLevel value() { return LogLevel.DEBUG; }
//            public String message() { return ""; }
//            public boolean logParams() { return true; }
//            public boolean logResult() { return true; }
//            public boolean logExecutionTime() { return false; }
//            public Class<? extends java.lang.annotation.Annotation> annotationType() { return LogMethod.class; }
//        };
//    }

    private LogMethod getEffectiveLogMethod(Method method, LogMethod methodAnnotation, Loggable classAnnotation) {
        if (methodAnnotation != null) {
            return methodAnnotation;
        }

        if (classAnnotation != null) {
            return new LogMethod() {
                @Override
                public Class<? extends Annotation> annotationType() {
                    return LogMethod.class;
                }

                @Override
                public LogLevel value() {
                    return classAnnotation.value();
                }

                @Override
                public String message() {
                    return "";
                }

                @Override
                public boolean logParams() {
                    return classAnnotation.logParams();
                }

                @Override
                public boolean logResult() {
                    return classAnnotation.logResults();
                }

                @Override
                public boolean logExecutionTime() {
                    return false;
                }
            };
        }

        // Default values if neither annotation is present
        return new LogMethod() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return LogMethod.class;
            }

            @Override
            public LogLevel value() {
                return LogLevel.DEBUG;
            }

            @Override
            public String message() {
                return "";
            }

            @Override
            public boolean logParams() {
                return true;
            }

            @Override
            public boolean logResult() {
                return true;
            }

            @Override
            public boolean logExecutionTime() {
                return false;
            }
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

        logger.log(logMethod.value(), message, null);
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

        logger.log(logMethod.value(), message.toString(),null);
    }
}
