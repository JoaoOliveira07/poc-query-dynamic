package com.example.poc.query.dynamic.usecase;

import com.example.poc.query.dynamic.dto.*;
import com.example.poc.query.dynamic.entity.Customer;
import com.example.poc.query.dynamic.service.DynamicQueryService;
import com.example.poc.query.dynamic.service.IndicatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SearchCustomerUseCase {

    private static final String BASE_QUERY = "customer_base_query";
    private static final String CONTEXT = "CUSTOMER";

    private final DynamicQueryService dynamicQueryService;
    private final IndicatorService indicatorService;

    /**
     * Executa a busca e retorna os dados junto com as opções disponíveis de filtros e ordenações
     */
    public CustomerSearchResponseDTO executeWithOptions(CustomerFilterDTO filter, Pageable pageable) {
        // Executar busca
        Page<CustomerDTO> customers = execute(filter, pageable);

        // Buscar opções disponíveis
        AvailableQueriesDTO availableQueries = dynamicQueryService.getAvailableQueriesByContext(CONTEXT);

        return CustomerSearchResponseDTO.builder()
                .customers(customers)
                .availableQueries(availableQueries)
                .build();
    }

    public Page<CustomerDTO> execute(CustomerFilterDTO filter, Pageable pageable) {
        // Construir a requisição dinâmica
        DynamicQueryRequestDTO request = buildDynamicRequest(filter, pageable);

        // Executar query
        List<?> results = dynamicQueryService.executeDynamicQuery(request);

        // Converter para CustomerDTO e adicionar indicadores
        List<CustomerDTO> customers = results.stream()
                .map(obj -> {
                    Customer customer = (Customer) obj;
                    CustomerDTO dto = CustomerDTO.fromEntity(customer);
                    // Avaliar e adicionar indicadores usando contexto CUSTOMER
                    dto.setIndicators(indicatorService.evaluateIndicators(customer, CONTEXT));
                    return dto;
                })
                .toList();

        // Buscar total de elementos para paginação
        Long total = dynamicQueryService.countDynamicQuery(request);

        return new PageImpl<>(customers, pageable, total);
    }


    private DynamicQueryRequestDTO buildDynamicRequest(CustomerFilterDTO filter, Pageable pageable) {
        // Construir o request com os filtros e ordenação vindos do frontend
        DynamicQueryRequestDTO.DynamicQueryRequestDTOBuilder builder = DynamicQueryRequestDTO.builder()
                .baseQueryName(BASE_QUERY)
                .filters(filter.getFilters() != null ? filter.getFilters() : List.of())
                .parameters(filter.getParameters() != null ? filter.getParameters() : Map.of());

        // Usar a ordenação vinda do filter, se existir
        if (filter.getOrder() != null && !filter.getOrder().isBlank()) {
            builder.order(filter.getOrder());
        }

        // Adicionar paginação se tiver Pageable
        if (pageable != null) {

            builder.page(pageable.getPageNumber())
                   .size(pageable.getPageSize());
        }

        return builder.build();
    }
}

