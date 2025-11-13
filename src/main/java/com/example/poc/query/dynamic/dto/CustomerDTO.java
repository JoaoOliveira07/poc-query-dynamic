package com.example.poc.query.dynamic.dto;

import com.example.poc.query.dynamic.entity.Customer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {

    private UUID id;
    private String name;
    private String tradeName;
    private String cnpj;
    private Boolean active;
    private Boolean blocked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<IndicatorDTO> indicators;

    public static CustomerDTO fromEntity(Customer customer) {
        return CustomerDTO.builder()
                .id(customer.getId())
                .name(customer.getName())
                .tradeName(customer.getTradeName())
                .cnpj(customer.getCnpj())
                .active(customer.getActive())
                .blocked(customer.getBlocked())
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .build();
    }
}

