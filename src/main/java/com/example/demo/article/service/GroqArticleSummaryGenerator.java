package com.example.demo.article.service;

import java.util.Arrays;
import java.util.List;

import com.example.demo.config.SummaryGenerationProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GroqArticleSummaryGenerator implements ArticleSummaryGenerator {

    private static final Logger log = LoggerFactory.getLogger(GroqArticleSummaryGenerator.class);

    private static final String SYSTEM_PROMPT = "You summarize news articles in clear neutral language. "
        + "Return plain text only, without bullet points, labels, or markdown.";

    private final SummaryGenerationProperties summaryGenerationProperties;

    @Override
    public String generateSummary(String content) {
        String normalizedContent = normalize(content);
        if (normalizedContent == null) {
            return "";
        }

        int maxWords = Math.max(summaryGenerationProperties.getMaxWords(), 1);
        SummaryGenerationProperties.Groq groq = summaryGenerationProperties.getGroq();

        if (isBlank(groq.getApiKey())) {
            log.warn("GROQ_API_KEY is not configured. Falling back to local summary generation.");
            return firstWords(normalizedContent, maxWords);
        }

        try {
            RestClient restClient = RestClient.builder()
                .baseUrl(trimTrailingSlash(defaultIfBlank(groq.getBaseUrl(), "https://api.groq.com/openai/v1")))
                .build();

            ChatCompletionRequest request = new ChatCompletionRequest(
                defaultIfBlank(groq.getModel(), "openai/gpt-oss-120b"),
                List.of(
                    new ChatMessage("system", SYSTEM_PROMPT),
                    new ChatMessage("user", buildUserPrompt(normalizedContent, maxWords))
                ),
                0.2,
                Math.max(maxWords * 3, 180)
            );

            ChatCompletionResponse response = restClient
                .post()
                .uri("/chat/completions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + groq.getApiKey().trim())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(ChatCompletionResponse.class);

            String generatedSummary = extractGeneratedSummary(response);
            if (isBlank(generatedSummary)) {
                log.warn("Groq returned an empty summary. Falling back to local summary generation.");
                return firstWords(normalizedContent, maxWords);
            }

            return firstWords(generatedSummary.trim(), maxWords);
        } catch (RuntimeException exception) {
            log.warn("Failed to generate summary with Groq. Falling back to local summary generation.", exception);
            return firstWords(normalizedContent, maxWords);
        }
    }

    private String extractGeneratedSummary(ChatCompletionResponse response) {
        if (response == null || response.choices() == null || response.choices().isEmpty()) {
            return null;
        }

        ChatChoice firstChoice = response.choices().get(0);
        if (firstChoice == null || firstChoice.message() == null) {
            return null;
        }

        return firstChoice.message().content();
    }

    private String buildUserPrompt(String content, int maxWords) {
        return "Summarize this news article in exactly " + maxWords
            + " words. Focus on key facts and keep a neutral tone.\n\n" + content;
    }

    private String firstWords(String value, int maxWords) {
        if (isBlank(value)) {
            return "";
        }

        String[] words = value.trim().split("\\s+");
        if (words.length <= maxWords) {
            return String.join(" ", words);
        }
        return String.join(" ", Arrays.copyOf(words, maxWords));
    }

    private String normalize(String value) {
        if (isBlank(value)) {
            return null;
        }
        return value.trim();
    }

    private String defaultIfBlank(String value, String fallback) {
        return isBlank(value) ? fallback : value.trim();
    }

    private String trimTrailingSlash(String value) {
        if (value.endsWith("/")) {
            return value.substring(0, value.length() - 1);
        }
        return value;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private record ChatCompletionRequest(
        String model,
        List<ChatMessage> messages,
        double temperature,
        @JsonProperty("max_tokens") int maxTokens
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ChatCompletionResponse(List<ChatChoice> choices) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ChatChoice(ChatMessage message) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ChatMessage(String role, String content) {
    }
}
