package com.ming.game.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebCorsConfig implements WebMvcConfigurer {
    @Value("${cors.allowedOrigins}")
    private String allowedOrig;

    @Value("${cors.maxAge}")
    private int age;

    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(allowedOrig)
                .allowedMethods("*")
                .allowCredentials(true)
                .maxAge(age);
    }
}
