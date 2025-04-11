package com.example.logging.aspect;

import com.example.logging.annotation.Loggable;
import com.example.logging.model.LogDetails;
import com.example.logging.service.LoggingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LoggingAspect {
    private final LoggingService loggingService;

    @Around("@annotation(loggable)")
    public Object logMethod(ProceedingJoinPoint joinPoint, Loggable loggable) throws Throwable {
        if (!loggingService.isLoggingEnabled()) {
            return joinPoint.proceed();
        }

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();

// Log method entry
        if (loggable.logParameters()) {
            Map<String, Object> parameters = getParameters(joinPoint);
            loggingService.log(LogDetails.builder()
                    .level(loggable.level())
                    .message("Entering method")
                    .className(className)
                    .methodName(methodName)
                    .parameters(parameters)
                    .build());
        } else {
            loggingService.log(LogDetails.builder()
                    .level(loggable.level())
                    .message("Entering method")
                    .className(className)
                    .methodName(methodName)
                    .build());
        }

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            Object result = joinPoint.proceed();
            stopWatch.stop();

// Log method exit
            if (loggable.logExecutionTime()) {
                loggingService.log(LogDetails.builder()
                        .level(loggable.level())
                        .message("Exiting method - execution time: " + stopWatch.getTotalTimeMillis() + " ms")
                        .className(className)
                        .methodName(methodName)
                        .build());
            }

            if (loggable.logResult()) {
                loggingService.log(LogDetails.builder()
                        .level(loggable.level())
                        .message("Method result")
                        .className(className)
                        .methodName(methodName)
                        .parameters(Map.of("result", result))
                        .build());
            }

            return result;
        } catch (Exception e) {
            stopWatch.stop();
            loggingService.log(LogDetails.builder()
                    .level("ERROR")
                    .message("Exception in method - execution time: " + stopWatch.getTotalTimeMillis() + " ms")
                    .className(className)
                    .methodName(methodName)
                    .exception(e.getClass().getName())
                    .stackTrace(getStackTraceAsString(e))
                    .build());
            throw e;
        }
    }

    private Map<String, Object> getParameters(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        Object[] parameterValues = joinPoint.getArgs();

        Map<String, Object> parameters = new HashMap<>();
        if (parameterNames != null && parameterNames.length > 0) {
            IntStream.range(0, parameterNames.length)
                    .forEach(i -> parameters.put(parameterNames[i], parameterValues[i]));
        }
        return parameters;
    }

    private String getStackTraceAsString(Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : throwable.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }
}
