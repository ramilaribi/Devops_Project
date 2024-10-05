package com.example.backend_voltix.aop;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.Arrays;

@Component
@Aspect
@Slf4j
public class AppLogger {
    @Around("execution(* com.example.backend_voltix.controller.AuthController.*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        logMethodStatus(joinPoint, "start");
        logMethodArguments(joinPoint);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object returnedValue = joinPoint.proceed();
        stopWatch.stop();
        logMethodExecutionTime(joinPoint, stopWatch.getTotalTimeMillis());
        logMethodStatus(joinPoint, "end");
        return returnedValue;
    }

    private void logMethodStatus(ProceedingJoinPoint joinPoint, String status) {
        log.info("{} :: {} :: {}",
                joinPoint.getTarget().getClass().getName(),
                joinPoint.getSignature().getName(),
                status);
    }

    private void logMethodArguments(ProceedingJoinPoint joinPoint) {
        log.info("executing function {} with arguments = {}",
                joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs()));
    }

    private void logMethodExecutionTime(ProceedingJoinPoint joinPoint, long executionTime) {
        log.info("{} :: {} :: execution time is =>{} ms",
                joinPoint.getTarget().getClass().getName(),
                joinPoint.getSignature().getName(),
                executionTime);
    }
}
