package com.example.poc.query.dynamic.service;

import com.example.poc.query.dynamic.dto.AvailableQueriesDTO;
import com.example.poc.query.dynamic.dto.DynamicQueryRequestDTO;
import com.example.poc.query.dynamic.dto.QueryMetadataDTO;
import com.example.poc.query.dynamic.entity.QueryBase;
import com.example.poc.query.dynamic.entity.QueryFilter;
import com.example.poc.query.dynamic.entity.QueryOrder;
import com.example.poc.query.dynamic.repository.QueryBaseRepository;
import com.example.poc.query.dynamic.repository.QueryFilterRepository;
import com.example.poc.query.dynamic.repository.QueryOrderRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DynamicQueryService {

    private final QueryBaseRepository queryBaseRepository;
    private final QueryFilterRepository queryFilterRepository;
    private final QueryOrderRepository queryOrderRepository;
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public AvailableQueriesDTO getAvailableQueriesByContext(String context) {
        List<QueryFilter> filters = queryFilterRepository.findByContextAndActiveTrue(context);
        List<QueryOrder> orders = queryOrderRepository.findByContextAndActiveTrue(context);

        return AvailableQueriesDTO.builder()
                .context(context)
                .filters(filters.stream()
                        .map(f -> QueryMetadataDTO.builder()
                                .nameUnique(f.getNameUnique())
                                .description(f.getDescription())
                                .build())
                        .toList())
                .orders(orders.stream()
                        .map(o -> QueryMetadataDTO.builder()
                                .nameUnique(o.getNameUnique())
                                .description(o.getDescription())
                                .build())
                        .toList())
                .build();
    }

    @Transactional(readOnly = true)
    public List<?> executeDynamicQuery(DynamicQueryRequestDTO request) {
        // 1. Buscar query base
        QueryBase baseQuery = queryBaseRepository.findByNameUniqueAndActiveTrue(request.getBaseQueryName())
                .orElseThrow(() -> new RuntimeException("Base query not found: " + request.getBaseQueryName()));

        // 2. Construir query completa
        StringBuilder fullQuery = new StringBuilder(baseQuery.getQuery());

        // 3. Adicionar filtros
        if (request.getFilters() != null && !request.getFilters().isEmpty()) {
            for (String filterName : request.getFilters()) {
                QueryFilter filter = queryFilterRepository.findByNameUniqueAndActiveTrue(filterName)
                        .orElseThrow(() -> new RuntimeException("Filter not found: " + filterName));

                fullQuery.append(" ").append(filter.getQueryFragment());
            }
        }

        // 4. Adicionar ordenação
        if (request.getOrder() != null && !request.getOrder().isBlank()) {
            QueryOrder order = queryOrderRepository.findByNameUniqueAndActiveTrue(request.getOrder())
                    .orElseThrow(() -> new RuntimeException("Order not found: " + request.getOrder()));

            fullQuery.append(" ").append(order.getQueryFragment());
        }

        log.info("Executing dynamic query: {}", fullQuery);

        // 5. Criar e configurar a query
        Query query = entityManager.createQuery(fullQuery.toString());

        // 6. Setar parâmetros
        if (request.getParameters() != null && !request.getParameters().isEmpty()) {
            for (Map.Entry<String, Object> param : request.getParameters().entrySet()) {
                Object convertedValue = convertParameterType(param.getKey(), param.getValue());
                query.setParameter(param.getKey(), convertedValue);
                log.info("Setting parameter: {} = {} (type: {})", param.getKey(), convertedValue,
                        convertedValue != null ? convertedValue.getClass().getSimpleName() : "null");
            }
        }

        // 7. Aplicar paginação se solicitado
        if (request.getPage() != null && request.getSize() != null) {
            query.setFirstResult(request.getPage() * request.getSize());
            query.setMaxResults(request.getSize());
        }

        // 8. Executar query
        return query.getResultList();
    }

    @Transactional(readOnly = true)
    public Long countDynamicQuery(DynamicQueryRequestDTO request) {
        // 1. Buscar query base
        QueryBase baseQuery = queryBaseRepository.findByNameUniqueAndActiveTrue(request.getBaseQueryName())
                .orElseThrow(() -> new RuntimeException("Base query not found: " + request.getBaseQueryName()));

        // 2. Construir query de contagem
        String originalQuery = baseQuery.getQuery();
        String countQuery = originalQuery.replaceFirst("SELECT .+ FROM", "SELECT COUNT(c) FROM");

        StringBuilder fullQuery = new StringBuilder(countQuery);

        // 3. Adicionar filtros
        if (request.getFilters() != null && !request.getFilters().isEmpty()) {
            for (String filterName : request.getFilters()) {
                QueryFilter filter = queryFilterRepository.findByNameUniqueAndActiveTrue(filterName)
                        .orElseThrow(() -> new RuntimeException("Filter not found: " + filterName));

                fullQuery.append(" ").append(filter.getQueryFragment());
            }
        }

        log.info("Executing count query: {}", fullQuery);

        // 4. Criar e configurar a query
        TypedQuery<Long> query = entityManager.createQuery(fullQuery.toString(), Long.class);

        // 5. Setar parâmetros
        if (request.getParameters() != null && !request.getParameters().isEmpty()) {
            for (Map.Entry<String, Object> param : request.getParameters().entrySet()) {
                Object convertedValue = convertParameterType(param.getKey(), param.getValue());
                query.setParameter(param.getKey(), convertedValue);
            }
        }

        return query.getSingleResult();
    }

    /**
     * Converte valores de parâmetros para os tipos corretos
     * Principalmente para tratar UUIDs que vem como String do JSON
     */
    private Object convertParameterType(String parameterName, Object value) {
        if (value == null) {
            return null;
        }

        // Se o parâmetro é "id" e o valor é String, converter para UUID
        if ("id".equals(parameterName) && value instanceof String) {
            try {
                return UUID.fromString((String) value);
            } catch (IllegalArgumentException e) {
                log.error("Failed to convert parameter '{}' value '{}' to UUID", parameterName, value);
                throw new RuntimeException("Invalid UUID format for parameter: " + parameterName, e);
            }
        }

        // Adicionar outras conversões conforme necessário
        // Por exemplo: datas, números, etc.

        return value;
    }
}
