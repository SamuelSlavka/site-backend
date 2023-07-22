package com.backend.api.logging;

import com.backend.api.wiki.controller.ArticleController;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.JoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private final Logger logger = LoggerFactory.getLogger((ArticleController.class));

    @Before("execution(* com.backend.api.wiki.service.ArticleService.createArticle(..))")
    public void beforeLogger(JoinPoint jp) {
        String arg = jp.getArgs()[0].toString();
        logger.debug("Article creation started {}", arg);
    }

    @After("execution(* com.backend.api.wiki.service.ArticleService.createArticle(..))")
    public void afterLogger() {
        logger.debug("Article created");
    }
}
