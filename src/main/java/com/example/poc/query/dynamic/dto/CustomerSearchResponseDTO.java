package com.example.poc.query.dynamic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerSearchResponseDTO {

    /**
     * Dados paginados dos customers
     */
    private Page<CustomerDTO> customers;

    /**
     * Opções de filtros e ordenações disponíveis
     */
    private AvailableQueriesDTO availableQueries;
}

