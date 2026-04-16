package com.example.demo.article.controller;

import java.util.List;

import com.example.demo.article.dto.ArticleResponse;
import com.example.demo.article.dto.ArticleSummaryResponse;
import com.example.demo.article.dto.CategoryResponse;
import com.example.demo.article.model.ArticleCategory;
import com.example.demo.article.service.ArticleService;
import com.example.demo.common.dto.PagedResponse;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/v1")
public class ArticleApiController {

    private final ArticleService articleService;

    @GetMapping("/feed")
    public PagedResponse<ArticleSummaryResponse> getFeed(
        @RequestParam(defaultValue = "1") @Min(1) int page,
        @RequestParam(defaultValue = "10") @Min(1) @Max(100) int limit,
        @RequestParam(required = false) ArticleCategory category,
        @RequestParam(required = false) String country,
        @RequestParam(required = false) String language
    ) {
        return articleService.getFeed(page, limit, category, country, language);
    }

    @GetMapping("/categories/{category}/feed")
    public PagedResponse<ArticleSummaryResponse> getFeedByCategory(
        @PathVariable ArticleCategory category,
        @RequestParam(defaultValue = "1") @Min(1) int page,
        @RequestParam(defaultValue = "10") @Min(1) @Max(100) int limit,
        @RequestParam(required = false) String country,
        @RequestParam(required = false) String language
    ) {
        return articleService.getFeed(page, limit, category, country, language);
    }

    @GetMapping("/articles/{id}")
    public ArticleResponse getArticle(@PathVariable Long id) {
        return articleService.getArticleById(id);
    }

    @GetMapping("/categories")
    public List<CategoryResponse> getCategories() {
        return articleService.getCategories();
    }

    @GetMapping("/search")
    public PagedResponse<ArticleSummaryResponse> search(
        @RequestParam("q") @NotBlank(message = "Search query is required.") String query,
        @RequestParam(defaultValue = "1") @Min(1) int page,
        @RequestParam(defaultValue = "10") @Min(1) @Max(100) int limit
    ) {
        return articleService.searchArticles(query, page, limit);
    }

    @GetMapping("/trending")
    public List<ArticleSummaryResponse> getTrending(
        @RequestParam(defaultValue = "5") @Min(1) @Max(20) int limit
    ) {
        return articleService.getTrendingArticles(limit);
    }
}
