package com.backend.api.logging;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.JoinPoint;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    @Before("execution(* com.backend.api.wiki.service.ArticleService.createArticle(..))")
    public void beforeLogger(JoinPoint jp) {
        String arg = jp.getArgs()[0].toString();
        System.out.println("Article creation started " + arg);
    }

    @After("execution(* com.backend.api.wiki.service.ArticleService.createArticle(..))")
    public void afterLogger() {
        System.out.println("Article created");
    }
}
