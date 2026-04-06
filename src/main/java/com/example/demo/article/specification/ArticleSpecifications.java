package com.example.demo.article.specification;

import com.example.demo.article.model.Article;
import com.example.demo.article.model.ArticleCategory;

import org.springframework.data.jpa.domain.Specification;

public final class ArticleSpecifications {

    private ArticleSpecifications() {
    }

    public static Specification<Article> hasCategory(ArticleCategory category) {
        return (root, query, builder) ->
            category == null ? builder.conjunction() : builder.equal(root.get("category"), category);
    }

    public static Specification<Article> hasCountry(String country) {
        return (root, query, builder) ->
            isBlank(country) ? builder.conjunction() : builder.equal(builder.lower(root.get("country")), country.trim().toLowerCase());
    }

    public static Specification<Article> hasLanguage(String language) {
        return (root, query, builder) ->
            isBlank(language) ? builder.conjunction() : builder.equal(builder.lower(root.get("language")), language.trim().toLowerCase());
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
