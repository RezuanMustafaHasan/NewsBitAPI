package com.example.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.security")
public class SecurityProperties {

    private Credentials admin = new Credentials();
    private Credentials publicUser = new Credentials();

    @Getter
    @Setter
    public static class Credentials {
        private String username;
        private String password;
    }
}
