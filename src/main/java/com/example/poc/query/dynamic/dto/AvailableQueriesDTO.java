package com.example.poc.query.dynamic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailableQueriesDTO {

    private String context;
    private List<QueryMetadataDTO> filters;
    private List<QueryMetadataDTO> orders;
}

