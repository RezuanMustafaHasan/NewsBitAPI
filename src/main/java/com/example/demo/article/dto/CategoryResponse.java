package com.example.demo.article.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CategoryResponse {
    String code;
    String label;
}
