package com.example.poc.query.dynamic.controller;

import com.example.poc.query.dynamic.dto.CustomerFilterDTO;
import com.example.poc.query.dynamic.dto.CustomerSearchResponseDTO;
import com.example.poc.query.dynamic.usecase.SearchCustomerUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final SearchCustomerUseCase searchCustomerUseCase;

    /**
     * Busca dinâmica de customers usando filtros e ordenações configurados no banco
     * Retorna os dados paginados E as opções disponíveis de filtros e ordenações
     */
    @PostMapping("/search")
    public ResponseEntity<CustomerSearchResponseDTO> searchCustomers(
            @RequestBody CustomerFilterDTO filter,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        CustomerSearchResponseDTO result = searchCustomerUseCase.executeWithOptions(filter, pageable);
        return ResponseEntity.ok(result);
    }

}

