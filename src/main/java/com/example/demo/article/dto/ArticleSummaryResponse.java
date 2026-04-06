package com.example.demo.article.dto;

import java.time.LocalDateTime;

import com.example.demo.article.model.ArticleCategory;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ArticleSummaryResponse {
    Long id;
    String title;
    String summary;
    String imageUrl;
    ArticleCategory category;
    String categoryLabel;
    String country;
    String language;
    LocalDateTime createdAt;
    Long views;
}
