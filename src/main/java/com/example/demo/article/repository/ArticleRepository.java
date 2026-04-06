package com.example.demo.article.repository;

import com.example.demo.article.model.Article;
import com.example.demo.article.model.ArticleCategory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArticleRepository extends JpaRepository<Article, Long>, JpaSpecificationExecutor<Article> {

    Page<Article> findByCategory(ArticleCategory category, Pageable pageable);

    @Query("""
        select article
        from Article article
        where lower(article.title) like lower(concat('%', :keyword, '%'))
           or lower(article.content) like lower(concat('%', :keyword, '%'))
        """)
    Page<Article> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    Page<Article> findAllByOrderByViewsDescCreatedAtDesc(Pageable pageable);
}
