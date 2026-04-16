package com.example.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.summary")
public class SummaryGenerationProperties {

    private int maxWords = 60;
    private Groq groq = new Groq();

    @Getter
    @Setter
    public static class Groq {
        private String apiKey;
        private String baseUrl = "https://api.groq.com/openai/v1";
        private String model = "openai/gpt-oss-120b";
    }
}
