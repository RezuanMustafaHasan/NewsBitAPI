package com.example.demo.article.service;

import java.util.Arrays;
import java.util.List;

import com.example.demo.article.dto.ArticleRequest;
import com.example.demo.article.dto.ArticleResponse;
import com.example.demo.article.dto.ArticleSummaryResponse;
import com.example.demo.article.dto.CategoryResponse;
import com.example.demo.article.model.Article;
import com.example.demo.article.model.ArticleCategory;
import com.example.demo.article.repository.ArticleRepository;
import com.example.demo.article.specification.ArticleSpecifications;
import com.example.demo.common.dto.PagedResponse;
import com.example.demo.exception.ResourceNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ArticleServiceImpl implements ArticleService {

    private static final Logger log = LoggerFactory.getLogger(ArticleServiceImpl.class);
    private static final Sort FEED_SORT = Sort.by(Sort.Direction.DESC, "createdAt");

    private final ArticleRepository articleRepository;

    @Override
    public ArticleResponse createArticle(ArticleRequest request) {
        Article article = mapToEntity(request, Article.builder().build());
        Article savedArticle = articleRepository.save(article);
        log.info("Created article with id={}", savedArticle.getId());
        return mapToResponse(savedArticle);
    }

    @Override
    public ArticleResponse updateArticle(Long id, ArticleRequest request) {
        Article article = findArticleEntity(id);
        Article updatedArticle = articleRepository.save(mapToEntity(request, article));
        log.info("Updated article with id={}", updatedArticle.getId());
        return mapToResponse(updatedArticle);
    }

    @Override
    public void deleteArticle(Long id) {
        Article article = findArticleEntity(id);
        articleRepository.delete(article);
        log.info("Deleted article with id={}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ArticleSummaryResponse> getFeed(int page, int limit, ArticleCategory category, String country, String language) {
        Pageable pageable = PageRequest.of(page, limit, FEED_SORT);
        Specification<Article> specification = Specification.allOf(
            ArticleSpecifications.hasCategory(category),
            ArticleSpecifications.hasCountry(country),
            ArticleSpecifications.hasLanguage(language)
        );
        Page<Article> articlePage = articleRepository.findAll(specification, pageable);
        return mapToPagedResponse(articlePage);
    }

    @Override
    public ArticleResponse getArticleById(Long id) {
        Article article = findArticleEntity(id);
        article.setViews(article.getViews() + 1);
        Article savedArticle = articleRepository.save(article);
        log.debug("Incremented views for article id={} to {}", id, savedArticle.getViews());
        return mapToResponse(savedArticle);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ArticleSummaryResponse> searchArticles(String keyword, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit, FEED_SORT);
        Page<Article> articlePage = articleRepository.searchByKeyword(keyword.trim(), pageable);
        return mapToPagedResponse(articlePage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArticleSummaryResponse> getTrendingArticles(int limit) {
        Page<Article> trendingPage = articleRepository.findAllByOrderByViewsDescCreatedAtDesc(PageRequest.of(0, limit));
        return trendingPage.stream().map(this::mapToSummary).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArticleSummaryResponse> getAdminArticles() {
        return articleRepository.findAll(FEED_SORT).stream().map(this::mapToSummary).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ArticleRequest getArticleForEdit(Long id) {
        Article article = findArticleEntity(id);
        return ArticleRequest.builder()
            .title(article.getTitle())
            .content(article.getContent())
            .summary(article.getSummary())
            .imageUrl(article.getImageUrl())
            .category(article.getCategory())
            .country(article.getCountry())
            .language(article.getLanguage())
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategories() {
        return Arrays.stream(ArticleCategory.values())
            .map(category -> CategoryResponse.builder()
                .code(category.name())
                .label(toCategoryLabel(category))
                .build())
            .toList();
    }

    private Article findArticleEntity(Long id) {
        return articleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Article not found with id " + id));
    }

    private Article mapToEntity(ArticleRequest request, Article article) {
        article.setTitle(request.getTitle().trim());
        article.setContent(request.getContent().trim());
        article.setSummary(request.getSummary().trim());
        article.setImageUrl(normalize(request.getImageUrl()));
        article.setCategory(request.getCategory());
        article.setCountry(normalize(request.getCountry()));
        article.setLanguage(normalize(request.getLanguage()));
        return article;
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private PagedResponse<ArticleSummaryResponse> mapToPagedResponse(Page<Article> page) {
        return PagedResponse.<ArticleSummaryResponse>builder()
            .content(page.stream().map(this::mapToSummary).toList())
            .page(page.getNumber())
            .limit(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .last(page.isLast())
            .build();
    }

    private ArticleResponse mapToResponse(Article article) {
        return ArticleResponse.builder()
            .id(article.getId())
            .title(article.getTitle())
            .content(article.getContent())
            .summary(article.getSummary())
            .imageUrl(article.getImageUrl())
            .category(article.getCategory())
            .categoryLabel(toCategoryLabel(article.getCategory()))
            .country(article.getCountry())
            .language(article.getLanguage())
            .createdAt(article.getCreatedAt())
            .views(article.getViews())
            .build();
    }

    private ArticleSummaryResponse mapToSummary(Article article) {
        return ArticleSummaryResponse.builder()
            .id(article.getId())
            .title(article.getTitle())
            .summary(article.getSummary())
            .imageUrl(article.getImageUrl())
            .category(article.getCategory())
            .categoryLabel(toCategoryLabel(article.getCategory()))
            .country(article.getCountry())
            .language(article.getLanguage())
            .createdAt(article.getCreatedAt())
            .views(article.getViews())
            .build();
    }

    private String toCategoryLabel(ArticleCategory category) {
        String value = category.name().toLowerCase();
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }
}
