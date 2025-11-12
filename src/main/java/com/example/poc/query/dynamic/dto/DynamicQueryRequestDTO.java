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
public class DynamicQueryRequestDTO {

    private String baseQueryName;
    private List<String> filters;
    private String order;
    private Map<String, Object> parameters;
    private Integer page;
    private Integer size;
}

