package com.example.poc.query.dynamic.service;

import com.example.poc.query.dynamic.dto.*;
import com.example.poc.query.dynamic.entity.QueryBase;
import com.example.poc.query.dynamic.entity.QueryFilter;
import com.example.poc.query.dynamic.entity.QueryOrder;
import com.example.poc.query.dynamic.repository.QueryBaseRepository;
import com.example.poc.query.dynamic.repository.QueryFilterRepository;
import com.example.poc.query.dynamic.repository.QueryOrderRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class QueryConfigService {

    private final QueryBaseRepository queryBaseRepository;
    private final QueryFilterRepository queryFilterRepository;
    private final QueryOrderRepository queryOrderRepository;

    @PersistenceContext
    private EntityManager entityManager;

    // QueryBase operations
    @Transactional(readOnly = true)
    public List<QueryBaseDTO> getAllQueryBases() {
        return queryBaseRepository.findAll().stream()
                .map(this::toQueryBaseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<QueryBaseDTO> getQueryBaseById(UUID id) {
        return queryBaseRepository.findById(id).map(this::toQueryBaseDTO);
    }

    @Transactional
    public QueryBaseDTO createQueryBase(QueryBaseDTO dto) {
        QueryBase entity = QueryBase.builder()
                .nameUnique(dto.getNameUnique())
                .context(dto.getContext())
                .query(dto.getQuery())
                .description(dto.getDescription())
                .active(dto.getActive() != null ? dto.getActive() : true)
                .build();
        return toQueryBaseDTO(queryBaseRepository.save(entity));
    }

    @Transactional
    public QueryBaseDTO updateQueryBase(UUID id, QueryBaseDTO dto) {
        QueryBase entity = queryBaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("QueryBase not found"));

        entity.setNameUnique(dto.getNameUnique());
        entity.setContext(dto.getContext());
        entity.setQuery(dto.getQuery());
        entity.setDescription(dto.getDescription());
        entity.setActive(dto.getActive());

        return toQueryBaseDTO(queryBaseRepository.save(entity));
    }

    @Transactional
    public void deleteQueryBase(UUID id) {
        queryBaseRepository.deleteById(id);
    }

    // QueryFilter operations
    @Transactional(readOnly = true)
    public List<QueryFilterDTO> getAllQueryFilters() {
        return queryFilterRepository.findAll().stream()
                .map(this::toQueryFilterDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<QueryFilterDTO> getQueryFilterById(UUID id) {
        return queryFilterRepository.findById(id).map(this::toQueryFilterDTO);
    }

    @Transactional
    public QueryFilterDTO createQueryFilter(QueryFilterDTO dto) {
        QueryFilter entity = QueryFilter.builder()
                .nameUnique(dto.getNameUnique())
                .context(dto.getContext())
                .queryFragment(dto.getQueryFragment())
                .description(dto.getDescription())
                .active(dto.getActive() != null ? dto.getActive() : true)
                .build();
        return toQueryFilterDTO(queryFilterRepository.save(entity));
    }

    @Transactional
    public QueryFilterDTO updateQueryFilter(UUID id, QueryFilterDTO dto) {
        QueryFilter entity = queryFilterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("QueryFilter not found"));

        entity.setNameUnique(dto.getNameUnique());
        entity.setContext(dto.getContext());
        entity.setQueryFragment(dto.getQueryFragment());
        entity.setDescription(dto.getDescription());
        entity.setActive(dto.getActive());

        return toQueryFilterDTO(queryFilterRepository.save(entity));
    }

    @Transactional
    public void deleteQueryFilter(UUID id) {
        queryFilterRepository.deleteById(id);
    }

    // QueryOrder operations
    @Transactional(readOnly = true)
    public List<QueryOrderDTO> getAllQueryOrders() {
        return queryOrderRepository.findAll().stream()
                .map(this::toQueryOrderDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<QueryOrderDTO> getQueryOrderById(UUID id) {
        return queryOrderRepository.findById(id).map(this::toQueryOrderDTO);
    }

    @Transactional
    public QueryOrderDTO createQueryOrder(QueryOrderDTO dto) {
        QueryOrder entity = QueryOrder.builder()
                .nameUnique(dto.getNameUnique())
                .context(dto.getContext())
                .queryFragment(dto.getQueryFragment())
                .description(dto.getDescription())
                .active(dto.getActive() != null ? dto.getActive() : true)
                .build();
        return toQueryOrderDTO(queryOrderRepository.save(entity));
    }

    @Transactional
    public QueryOrderDTO updateQueryOrder(UUID id, QueryOrderDTO dto) {
        QueryOrder entity = queryOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("QueryOrder not found"));

        entity.setNameUnique(dto.getNameUnique());
        entity.setContext(dto.getContext());
        entity.setQueryFragment(dto.getQueryFragment());
        entity.setDescription(dto.getDescription());
        entity.setActive(dto.getActive());

        return toQueryOrderDTO(queryOrderRepository.save(entity));
    }

    @Transactional
    public void deleteQueryOrder(UUID id) {
        queryOrderRepository.deleteById(id);
    }

    // Query Preview
    @Transactional(readOnly = true)
    public QueryPreviewResponse previewQuery(QueryPreviewRequest request) {
        try {
            StringBuilder finalQuery = new StringBuilder(request.getBaseQuery());

            // Add filters - just append them since they should contain "AND" or "WHERE" if needed
            if (request.getFilters() != null && !request.getFilters().isEmpty()) {
                for (String filter : request.getFilters()) {
                    finalQuery.append(" ").append(filter);
                }
            }

            // Add orders - just append them since they should contain "ORDER BY" if needed
            if (request.getOrders() != null && !request.getOrders().isEmpty()) {
                for (String order : request.getOrders()) {
                    finalQuery.append(" ").append(order);
                }
            }

            String queryString = finalQuery.toString();
            log.info("Preview query: {}", queryString);

            // Try to execute with limit for preview
            // Use createQuery instead of createNativeQuery to support JPQL (camelCase properties)
            Query query = entityManager.createQuery(queryString);

            // Set parameters if provided
            if (request.getParameters() != null) {
                request.getParameters().forEach(query::setParameter);
            }

            query.setMaxResults(10); // Limit preview to 10 results

            @SuppressWarnings("unchecked")
            List<Object> results = query.getResultList();

            // Convert results to map
            List<Map<String, Object>> previewResults = new ArrayList<>();
            for (Object result : results) {
                Map<String, Object> row = new LinkedHashMap<>();
                if (result instanceof Object[] cols) {
                    // Multiple columns selected (e.g., SELECT c.name, c.cnpj FROM ...)
                    for (int i = 0; i < cols.length; i++) {
                        row.put("column_" + (i + 1), cols[i]);
                    }
                } else {
                    // Single entity or value selected (e.g., SELECT c FROM ...)
                    // Convert entity to map using reflection
                    row = convertEntityToMap(result);
                }
                previewResults.add(row);
            }

            // Get total count (without limit)
            Query countQuery = entityManager.createQuery(queryString);
            if (request.getParameters() != null) {
                request.getParameters().forEach(countQuery::setParameter);
            }
            int totalResults = countQuery.getResultList().size();

            return QueryPreviewResponse.builder()
                    .finalQuery(queryString)
                    .valid(true)
                    .previewResults(previewResults)
                    .totalResults(totalResults)
                    .build();

        } catch (Exception e) {
            log.error("Error previewing query", e);
            return QueryPreviewResponse.builder()
                    .finalQuery(request.getBaseQuery())
                    .valid(false)
                    .errorMessage(e.getMessage())
                    .previewResults(Collections.emptyList())
                    .totalResults(0)
                    .build();
        }
    }

    // Helper method to convert entity to map
    private Map<String, Object> convertEntityToMap(Object entity) {
        Map<String, Object> map = new LinkedHashMap<>();

        if (entity == null) {
            return map;
        }

        // Handle primitive types and strings
        if (entity instanceof String || entity instanceof Number || entity instanceof Boolean) {
            map.put("value", entity);
            return map;
        }

        // Use reflection to get all fields from the entity
        try {
            Class<?> clazz = entity.getClass();
            java.lang.reflect.Field[] fields = clazz.getDeclaredFields();

            for (java.lang.reflect.Field field : fields) {
                field.setAccessible(true);
                try {
                    Object value = field.get(entity);
                    map.put(field.getName(), value);
                } catch (IllegalAccessException e) {
                    log.warn("Could not access field: {}", field.getName());
                }
            }
        } catch (Exception e) {
            log.error("Error converting entity to map", e);
            map.put("error", "Could not convert entity: " + e.getMessage());
        }

        return map;
    }

    // Mappers
    private QueryBaseDTO toQueryBaseDTO(QueryBase entity) {
        return QueryBaseDTO.builder()
                .id(entity.getId())
                .nameUnique(entity.getNameUnique())
                .context(entity.getContext())
                .query(entity.getQuery())
                .description(entity.getDescription())
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private QueryFilterDTO toQueryFilterDTO(QueryFilter entity) {
        return QueryFilterDTO.builder()
                .id(entity.getId())
                .nameUnique(entity.getNameUnique())
                .context(entity.getContext())
                .queryFragment(entity.getQueryFragment())
                .description(entity.getDescription())
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private QueryOrderDTO toQueryOrderDTO(QueryOrder entity) {
        return QueryOrderDTO.builder()
                .id(entity.getId())
                .nameUnique(entity.getNameUnique())
                .context(entity.getContext())
                .queryFragment(entity.getQueryFragment())
                .description(entity.getDescription())
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}

