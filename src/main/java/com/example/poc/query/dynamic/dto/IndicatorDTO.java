package com.example.poc.query.dynamic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO genérico para representar um indicador de qualquer entidade
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IndicatorDTO {

    private String key;
    private String name;
    private Boolean value;
    private String icon;
    private String description;
}

