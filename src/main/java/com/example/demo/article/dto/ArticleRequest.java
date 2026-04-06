package com.example.demo.article.dto;

import com.example.demo.article.model.ArticleCategory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleRequest {

    @NotBlank(message = "Title is required.")
    @Size(max = 255, message = "Title must be at most 255 characters.")
    private String title;

    @NotBlank(message = "Content is required.")
    private String content;

    @NotBlank(message = "Summary is required.")
    @Size(max = 500, message = "Summary must be at most 500 characters.")
    private String summary;

    @Size(max = 500, message = "Image URL must be at most 500 characters.")
    private String imageUrl;

    @NotNull(message = "Category is required.")
    private ArticleCategory category;

    @Size(max = 100, message = "Country must be at most 100 characters.")
    private String country;

    @Size(max = 20, message = "Language must be at most 20 characters.")
    private String language;
}
