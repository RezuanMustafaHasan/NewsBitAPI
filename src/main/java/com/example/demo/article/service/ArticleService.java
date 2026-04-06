package com.example.demo.article.service;

import java.util.List;

import com.example.demo.article.dto.ArticleRequest;
import com.example.demo.article.dto.ArticleResponse;
import com.example.demo.article.dto.ArticleSummaryResponse;
import com.example.demo.article.dto.CategoryResponse;
import com.example.demo.article.model.ArticleCategory;
import com.example.demo.common.dto.PagedResponse;

public interface ArticleService {

    ArticleResponse createArticle(ArticleRequest request);

    ArticleResponse updateArticle(Long id, ArticleRequest request);

    void deleteArticle(Long id);

    PagedResponse<ArticleSummaryResponse> getFeed(int page, int limit, ArticleCategory category, String country, String language);

    ArticleResponse getArticleById(Long id);

    PagedResponse<ArticleSummaryResponse> searchArticles(String keyword, int page, int limit);

    List<ArticleSummaryResponse> getTrendingArticles(int limit);

    List<ArticleSummaryResponse> getAdminArticles();

    ArticleRequest getArticleForEdit(Long id);

    List<CategoryResponse> getCategories();
}
