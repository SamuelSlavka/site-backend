package com.backend.api.auth;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuthAspect {
    @Pointcut("within(com.backend.api.notes..*)")
    public void authenticationPointCut() {
        System.out.println("Note creation started");
    }

    @Pointcut("within(com.backend.api.notes..*)")
    public void authorizationPointCut() {
        System.out.println("Note creation started");
    }
    @Before("authenticationPointCut() && authorizationPointCut()")
    public void authenticate() {
        System.out.println("Authenticating");
    }
}
