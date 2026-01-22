package com.loopon.global.log;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.HashMap;
import java.util.Map;

@Component
@Aspect
@Slf4j
public class LogAspect {

    private static final String[] SENSITIVE_KEYWORDS = {
            "password", "pw", "secret",
            "token", "access", "refresh",
            "auth", "cred",
            "key", "pin", "card",
            "ssn", "social"
    };

    @Pointcut("execution(* com.loopon..*Controller.*(..))")
    public void controllerMethods() {
    }

    @Pointcut("execution(* com.loopon..*Service.*(..))")
    public void serviceMethods() {
    }

    @Around("controllerMethods() || serviceMethods()")
    public Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        MethodInfo methodInfo = null;

        try {
            methodInfo = extractMethodInfo(joinPoint);

            stopWatch.start();
            Object result = joinPoint.proceed();
            stopWatch.stop();

            logSuccess(methodInfo, result, stopWatch);
            return result;

        } catch (Throwable e) {
            if (stopWatch.isRunning()) {
                stopWatch.stop();
            }

            logFailure(methodInfo, e, stopWatch);
            throw e;
        }
    }

    private void logSuccess(MethodInfo methodInfo, Object result, StopWatch stopWatch) {
        log.info("{} | {}({}) -> {} [{}ms]",
                methodInfo.className(),
                methodInfo.methodName(),
                methodInfo.params(),
                formatResult(result),
                stopWatch.getTotalTimeMillis());
    }

    private void logFailure(MethodInfo info, Throwable e, StopWatch stopWatch) {
        String className = (info != null) ? info.className() : "UnknownClass";
        String methodName = (info != null) ? info.methodName() : "UnknownMethod";
        Map<String, Object> params = (info != null) ? info.params() : Map.of();

        log.error("{} | {}({}) -> [EXCEPTION] {}: {} [{}ms]",
                className,
                methodName,
                params,
                e.getClass().getSimpleName(),
                e.getMessage(),
                stopWatch.getTotalTimeMillis()
        );
    }

    private MethodInfo extractMethodInfo(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();
        Map<String, Object> params = extractParams(joinPoint);

        return new MethodInfo(className, methodName, params);
    }

    private Map<String, Object> extractParams(ProceedingJoinPoint joinPoint) {
        CodeSignature signature = (CodeSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        Map<String, Object> params = new HashMap<>();

        if (paramNames == null) return params;

        for (int i = 0; i < paramNames.length; i++) {
            String key = paramNames[i];
            Object value = args[i];
            if (isSensitive(key)) {
                params.put(key, "****");
            } else {
                params.put(key, value);
            }
        }
        return params;
    }

    private boolean isSensitive(String key) {
        if (key == null) return false;

        String lowerKey = key.toLowerCase();

        for (String keyword : SENSITIVE_KEYWORDS) {
            if (lowerKey.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private String formatResult(Object result) {
        if (result == null) return "void";
        String resultStr = result.toString();
        if (resultStr.length() > 100) {
            return resultStr.substring(0, 100) + "...";
        }
        return resultStr;
    }

    private record MethodInfo(String className, String methodName, Map<String, Object> params) {}
}
