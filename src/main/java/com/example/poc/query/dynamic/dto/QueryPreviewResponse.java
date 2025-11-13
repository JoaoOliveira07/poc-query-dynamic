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
public class QueryPreviewResponse {
    private String finalQuery;
    private boolean valid;
    private String errorMessage;
    private List<Map<String, Object>> previewResults;
    private int totalResults;
}
