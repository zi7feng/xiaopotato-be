package com.fzq.springboottemplate.aspect;

import com.fzq.springboottemplate.model.other.LogBean;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;

@Aspect
@Component
public class LoggingAspect {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Pointcut("execution(* com.fzq.springboottemplate.service.*.*(..))")
    public void serviceMethods() {

    }

    @AfterReturning(pointcut = "serviceMethods()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        LogBean log = new LogBean();
        log.setCreateDate(new Date());
        log.setClassName(joinPoint.getTarget().getClass().getSimpleName());
        log.setMethod(joinPoint.getSignature().getName());
        if (result != null) {
            log.setReqParam(result.toString());
        }
        mongoTemplate.save(log);
    }
}