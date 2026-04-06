package com.example.demo.common.dto;

import java.util.List;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PagedResponse<T> {
    List<T> content;
    int page;
    int limit;
    long totalElements;
    int totalPages;
    boolean last;
}
