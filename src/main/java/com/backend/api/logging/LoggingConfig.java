package com.backend.api.logging;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@ComponentScan(basePackages = "logging")
@EnableAspectJAutoProxy
public class LoggingConfig {
}
