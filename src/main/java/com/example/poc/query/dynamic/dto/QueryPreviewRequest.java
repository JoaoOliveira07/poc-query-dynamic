package com.example.poc.query.dynamic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryPreviewRequest {
    private String baseQuery;
    private List<String> filters;
    private List<String> orders;
    private Map<String, Object> parameters;
}

