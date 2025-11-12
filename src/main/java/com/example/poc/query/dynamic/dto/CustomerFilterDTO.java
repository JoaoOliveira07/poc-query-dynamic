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
public class CustomerFilterDTO {

    /**
     * Lista de nomes únicos dos filtros a serem aplicados (ex: "only_customer_actives", "customer_by_name")
     */
    private List<String> filters;

    /**
     * Nome único da ordenação a ser aplicada (ex: "customer_order_by_name_asc")
     */
    private String order;

    /**
     * Parâmetros necessários para os filtros (ex: {"name": "João", "cnpj": "12345678000100"})
     */
    private Map<String, Object> parameters;
}

