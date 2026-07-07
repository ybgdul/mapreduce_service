package mapreduce.app.components;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ServiceAspect {
    
    private static Logger log = LoggerFactory.getLogger(ServiceAspect.class);
    
    @Pointcut("@within(org.springframework.stereotype.Service)")
    public void serviceMethods(){}

    @Before("serviceMethods()")
    public void logBefore(JoinPoint joinPoint) {
        log.info("About to execute: " + joinPoint.getSignature().getName());
    }

    @After("serviceMethods()")
    public void logAfter(JoinPoint joinPoint) { 
        log.info("After executing: " + joinPoint.getSignature().getName());
    }

    @AfterThrowing(pointcut="serviceMethods()", throwing="error")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable error) { 
        log.error("Error thrown after executing: " + joinPoint.getSignature().getName() + ", the error: " + error);
    }

    @AfterReturning(pointcut="serviceMethods()", returning="result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("Returning after executing: " + joinPoint.getSignature().getName() + ", returned: " + result);
    }

    @Around("serviceMethods()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("About to execute: " + joinPoint.getSignature().getName());
        Object res = null;
        try {
            res = joinPoint.proceed();
            log.info("After returning method: " + joinPoint.getSignature().getName());
        } catch (Throwable e) {
            log.error("Method threw an exception: " + joinPoint.getSignature().getName() + ", error: " + e );
            throw e;
        }

        log.info("After the method execution: " + joinPoint.getSignature().getName());
        return res;
    }

}
