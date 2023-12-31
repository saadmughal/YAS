package com.yas.product.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

@TestConfiguration
public class SecurityConfig {

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers(HttpMethod.POST, "/backoffice/**")
                .requestMatchers(HttpMethod.PUT, "/backoffice/**")
                .requestMatchers(HttpMethod.GET, "/backoffice/**")
                .requestMatchers(HttpMethod.DELETE, "/backoffice/**");
    }
}
