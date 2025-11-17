package com.example.poc.query.dynamic.service;

import com.example.poc.query.dynamic.dto.*;
import com.example.poc.query.dynamic.entity.QueryBase;
import com.example.poc.query.dynamic.entity.QueryFilter;
import com.example.poc.query.dynamic.entity.QueryOrder;
import com.example.poc.query.dynamic.repository.QueryBaseRepository;
import com.example.poc.query.dynamic.repository.QueryFilterRepository;
import com.example.poc.query.dynamic.repository.QueryOrderRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("QueryConfigService Tests")
class QueryConfigServiceTest {

    @Mock
    private QueryBaseRepository queryBaseRepository;

    @Mock
    private QueryFilterRepository queryFilterRepository;

    @Mock
    private QueryOrderRepository queryOrderRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private QueryConfigService queryConfigService;

    private UUID testId;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        now = LocalDateTime.now();
    }

    @Nested
    @DisplayName("QueryBase Operations")
    class QueryBaseOperations {

        @Test
        @DisplayName("Should get all query bases successfully")
        void shouldGetAllQueryBases() {
            // Given
            QueryBase entity1 = createQueryBaseEntity("query1", "context1");
            QueryBase entity2 = createQueryBaseEntity("query2", "context2");
            when(queryBaseRepository.findAll()).thenReturn(Arrays.asList(entity1, entity2));

            // When
            List<QueryBaseDTO> result = queryConfigService.getAllQueryBases();

            // Then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getNameUnique()).isEqualTo("query1");
            assertThat(result.get(1).getNameUnique()).isEqualTo("query2");
            verify(queryBaseRepository).findAll();
        }

        @Test
        @DisplayName("Should get query base by id successfully")
        void shouldGetQueryBaseById() {
            // Given
            QueryBase entity = createQueryBaseEntity("query1", "context1");
            when(queryBaseRepository.findById(testId)).thenReturn(Optional.of(entity));

            // When
            Optional<QueryBaseDTO> result = queryConfigService.getQueryBaseById(testId);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getNameUnique()).isEqualTo("query1");
            verify(queryBaseRepository).findById(testId);
        }

        @Test
        @DisplayName("Should return empty when query base not found")
        void shouldReturnEmptyWhenQueryBaseNotFound() {
            // Given
            when(queryBaseRepository.findById(testId)).thenReturn(Optional.empty());

            // When
            Optional<QueryBaseDTO> result = queryConfigService.getQueryBaseById(testId);

            // Then
            assertThat(result).isEmpty();
            verify(queryBaseRepository).findById(testId);
        }

        @Test
        @DisplayName("Should create query base successfully")
        void shouldCreateQueryBase() {
            // Given
            QueryBaseDTO dto = createQueryBaseDTO("query1", "context1", true);
            QueryBase savedEntity = createQueryBaseEntity("query1", "context1");

            when(queryBaseRepository.save(any(QueryBase.class))).thenReturn(savedEntity);

            // When
            QueryBaseDTO result = queryConfigService.createQueryBase(dto);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getNameUnique()).isEqualTo("query1");
            assertThat(result.getContext()).isEqualTo("context1");

            ArgumentCaptor<QueryBase> captor = ArgumentCaptor.forClass(QueryBase.class);
            verify(queryBaseRepository).save(captor.capture());
            QueryBase captured = captor.getValue();
            assertThat(captured.getNameUnique()).isEqualTo("query1");
            assertThat(captured.getActive()).isTrue();
        }

        @Test
        @DisplayName("Should create query base with default active true when active is null")
        void shouldCreateQueryBaseWithDefaultActive() {
            // Given
            QueryBaseDTO dto = createQueryBaseDTO("query1", "context1", null);
            QueryBase savedEntity = createQueryBaseEntity("query1", "context1");

            when(queryBaseRepository.save(any(QueryBase.class))).thenReturn(savedEntity);

            // When
            queryConfigService.createQueryBase(dto);

            // Then
            ArgumentCaptor<QueryBase> captor = ArgumentCaptor.forClass(QueryBase.class);
            verify(queryBaseRepository).save(captor.capture());
            assertThat(captor.getValue().getActive()).isTrue();
        }

        @Test
        @DisplayName("Should update query base successfully")
        void shouldUpdateQueryBase() {
            // Given
            QueryBaseDTO dto = createQueryBaseDTO("updated_query", "updated_context", false);
            QueryBase existingEntity = createQueryBaseEntity("query1", "context1");

            when(queryBaseRepository.findById(testId)).thenReturn(Optional.of(existingEntity));
            when(queryBaseRepository.save(any(QueryBase.class))).thenReturn(existingEntity);

            // When
            QueryBaseDTO result = queryConfigService.updateQueryBase(testId, dto);

            // Then
            assertThat(result).isNotNull();
            verify(queryBaseRepository).findById(testId);
            verify(queryBaseRepository).save(existingEntity);
            assertThat(existingEntity.getNameUnique()).isEqualTo("updated_query");
            assertThat(existingEntity.getContext()).isEqualTo("updated_context");
            assertThat(existingEntity.getActive()).isFalse();
        }

        @Test
        @DisplayName("Should throw exception when updating non-existent query base")
        void shouldThrowExceptionWhenUpdatingNonExistentQueryBase() {
            // Given
            QueryBaseDTO dto = createQueryBaseDTO("query1", "context1", true);
            when(queryBaseRepository.findById(testId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> queryConfigService.updateQueryBase(testId, dto))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("QueryBase not found");

            verify(queryBaseRepository).findById(testId);
            verify(queryBaseRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should delete query base successfully")
        void shouldDeleteQueryBase() {
            // When
            queryConfigService.deleteQueryBase(testId);

            // Then
            verify(queryBaseRepository).deleteById(testId);
        }
    }

    @Nested
    @DisplayName("QueryFilter Operations")
    class QueryFilterOperations {

        @Test
        @DisplayName("Should get all query filters successfully")
        void shouldGetAllQueryFilters() {
            // Given
            QueryFilter entity1 = createQueryFilterEntity("filter1", "context1");
            QueryFilter entity2 = createQueryFilterEntity("filter2", "context2");
            when(queryFilterRepository.findAll()).thenReturn(Arrays.asList(entity1, entity2));

            // When
            List<QueryFilterDTO> result = queryConfigService.getAllQueryFilters();

            // Then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getNameUnique()).isEqualTo("filter1");
            assertThat(result.get(1).getNameUnique()).isEqualTo("filter2");
            verify(queryFilterRepository).findAll();
        }

        @Test
        @DisplayName("Should get query filter by id successfully")
        void shouldGetQueryFilterById() {
            // Given
            QueryFilter entity = createQueryFilterEntity("filter1", "context1");
            when(queryFilterRepository.findById(testId)).thenReturn(Optional.of(entity));

            // When
            Optional<QueryFilterDTO> result = queryConfigService.getQueryFilterById(testId);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getNameUnique()).isEqualTo("filter1");
            verify(queryFilterRepository).findById(testId);
        }

        @Test
        @DisplayName("Should create query filter successfully")
        void shouldCreateQueryFilter() {
            // Given
            QueryFilterDTO dto = createQueryFilterDTO("filter1", "context1", true);
            QueryFilter savedEntity = createQueryFilterEntity("filter1", "context1");

            when(queryFilterRepository.save(any(QueryFilter.class))).thenReturn(savedEntity);

            // When
            QueryFilterDTO result = queryConfigService.createQueryFilter(dto);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getNameUnique()).isEqualTo("filter1");

            ArgumentCaptor<QueryFilter> captor = ArgumentCaptor.forClass(QueryFilter.class);
            verify(queryFilterRepository).save(captor.capture());
            assertThat(captor.getValue().getNameUnique()).isEqualTo("filter1");
        }

        @Test
        @DisplayName("Should update query filter successfully")
        void shouldUpdateQueryFilter() {
            // Given
            QueryFilterDTO dto = createQueryFilterDTO("updated_filter", "updated_context", false);
            QueryFilter existingEntity = createQueryFilterEntity("filter1", "context1");

            when(queryFilterRepository.findById(testId)).thenReturn(Optional.of(existingEntity));
            when(queryFilterRepository.save(any(QueryFilter.class))).thenReturn(existingEntity);

            // When
            QueryFilterDTO result = queryConfigService.updateQueryFilter(testId, dto);

            // Then
            assertThat(result).isNotNull();
            verify(queryFilterRepository).findById(testId);
            verify(queryFilterRepository).save(existingEntity);
            assertThat(existingEntity.getNameUnique()).isEqualTo("updated_filter");
        }

        @Test
        @DisplayName("Should throw exception when updating non-existent query filter")
        void shouldThrowExceptionWhenUpdatingNonExistentQueryFilter() {
            // Given
            QueryFilterDTO dto = createQueryFilterDTO("filter1", "context1", true);
            when(queryFilterRepository.findById(testId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> queryConfigService.updateQueryFilter(testId, dto))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("QueryFilter not found");
        }

        @Test
        @DisplayName("Should delete query filter successfully")
        void shouldDeleteQueryFilter() {
            // When
            queryConfigService.deleteQueryFilter(testId);

            // Then
            verify(queryFilterRepository).deleteById(testId);
        }
    }

    @Nested
    @DisplayName("QueryOrder Operations")
    class QueryOrderOperations {

        @Test
        @DisplayName("Should get all query orders successfully")
        void shouldGetAllQueryOrders() {
            // Given
            QueryOrder entity1 = createQueryOrderEntity("order1", "context1");
            QueryOrder entity2 = createQueryOrderEntity("order2", "context2");
            when(queryOrderRepository.findAll()).thenReturn(Arrays.asList(entity1, entity2));

            // When
            List<QueryOrderDTO> result = queryConfigService.getAllQueryOrders();

            // Then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getNameUnique()).isEqualTo("order1");
            assertThat(result.get(1).getNameUnique()).isEqualTo("order2");
            verify(queryOrderRepository).findAll();
        }

        @Test
        @DisplayName("Should get query order by id successfully")
        void shouldGetQueryOrderById() {
            // Given
            QueryOrder entity = createQueryOrderEntity("order1", "context1");
            when(queryOrderRepository.findById(testId)).thenReturn(Optional.of(entity));

            // When
            Optional<QueryOrderDTO> result = queryConfigService.getQueryOrderById(testId);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getNameUnique()).isEqualTo("order1");
            verify(queryOrderRepository).findById(testId);
        }

        @Test
        @DisplayName("Should create query order successfully")
        void shouldCreateQueryOrder() {
            // Given
            QueryOrderDTO dto = createQueryOrderDTO("order1", "context1", true);
            QueryOrder savedEntity = createQueryOrderEntity("order1", "context1");

            when(queryOrderRepository.save(any(QueryOrder.class))).thenReturn(savedEntity);

            // When
            QueryOrderDTO result = queryConfigService.createQueryOrder(dto);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getNameUnique()).isEqualTo("order1");

            ArgumentCaptor<QueryOrder> captor = ArgumentCaptor.forClass(QueryOrder.class);
            verify(queryOrderRepository).save(captor.capture());
            assertThat(captor.getValue().getNameUnique()).isEqualTo("order1");
        }

        @Test
        @DisplayName("Should update query order successfully")
        void shouldUpdateQueryOrder() {
            // Given
            QueryOrderDTO dto = createQueryOrderDTO("updated_order", "updated_context", false);
            QueryOrder existingEntity = createQueryOrderEntity("order1", "context1");

            when(queryOrderRepository.findById(testId)).thenReturn(Optional.of(existingEntity));
            when(queryOrderRepository.save(any(QueryOrder.class))).thenReturn(existingEntity);

            // When
            QueryOrderDTO result = queryConfigService.updateQueryOrder(testId, dto);

            // Then
            assertThat(result).isNotNull();
            verify(queryOrderRepository).findById(testId);
            verify(queryOrderRepository).save(existingEntity);
            assertThat(existingEntity.getNameUnique()).isEqualTo("updated_order");
        }

        @Test
        @DisplayName("Should throw exception when updating non-existent query order")
        void shouldThrowExceptionWhenUpdatingNonExistentQueryOrder() {
            // Given
            QueryOrderDTO dto = createQueryOrderDTO("order1", "context1", true);
            when(queryOrderRepository.findById(testId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> queryConfigService.updateQueryOrder(testId, dto))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("QueryOrder not found");
        }

        @Test
        @DisplayName("Should delete query order successfully")
        void shouldDeleteQueryOrder() {
            // When
            queryConfigService.deleteQueryOrder(testId);

            // Then
            verify(queryOrderRepository).deleteById(testId);
        }
    }

    @Nested
    @DisplayName("Query Preview Operations")
    class QueryPreviewOperations {

        private Query query;

        @BeforeEach
        void setUpPreview() {
            query = mock(Query.class);
            // Manually inject the EntityManager mock into the service
            ReflectionTestUtils.setField(queryConfigService, "entityManager", entityManager);
        }

        @Test
        @DisplayName("Should preview query successfully with single value results")
        void shouldPreviewQueryWithSingleValues() {
            // Given
            QueryPreviewRequest request = QueryPreviewRequest.builder()
                    .baseQuery("SELECT * FROM customer")
                    .build();

            List<Object> mockResults = Arrays.asList("result1", "result2", "result3");

            when(entityManager.createNativeQuery(anyString())).thenReturn(query);
            when(query.getResultList()).thenReturn(mockResults);

            // When
            QueryPreviewResponse result = queryConfigService.previewQuery(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.isValid()).isTrue();
            assertThat(result.getFinalQuery()).isEqualTo("SELECT * FROM customer");
            assertThat(result.getPreviewResults()).hasSize(3);
            assertThat(result.getPreviewResults().get(0).get("result")).isEqualTo("result1");
            assertThat(result.getTotalResults()).isEqualTo(3);

            verify(query).setMaxResults(10);
            verify(entityManager, times(2)).createNativeQuery(anyString());
        }

        @Test
        @DisplayName("Should preview query successfully with array results")
        void shouldPreviewQueryWithArrayResults() {
            // Given
            QueryPreviewRequest request = QueryPreviewRequest.builder()
                    .baseQuery("SELECT id, name FROM customer")
                    .build();

            Object[] row1 = new Object[]{1, "John"};
            Object[] row2 = new Object[]{2, "Jane"};
            List<Object> mockResults = Arrays.asList(row1, row2);

            when(entityManager.createNativeQuery(anyString())).thenReturn(query);
            when(query.getResultList()).thenReturn(mockResults);

            // When
            QueryPreviewResponse result = queryConfigService.previewQuery(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.isValid()).isTrue();
            assertThat(result.getPreviewResults()).hasSize(2);
            assertThat(result.getPreviewResults().get(0).get("column_1")).isEqualTo(1);
            assertThat(result.getPreviewResults().get(0).get("column_2")).isEqualTo("John");
            assertThat(result.getPreviewResults().get(1).get("column_1")).isEqualTo(2);
            assertThat(result.getPreviewResults().get(1).get("column_2")).isEqualTo("Jane");
        }

        @Test
        @DisplayName("Should preview query with filters and orders")
        void shouldPreviewQueryWithFiltersAndOrders() {
            // Given
            QueryPreviewRequest request = QueryPreviewRequest.builder()
                    .baseQuery("SELECT * FROM customer")
                    .filters(Arrays.asList("WHERE active = true", "AND age > 18"))
                    .orders(Collections.singletonList("ORDER BY name ASC"))
                    .build();

            List<Object> mockResults = Collections.singletonList("result");

            when(entityManager.createNativeQuery(anyString())).thenReturn(query);
            when(query.getResultList()).thenReturn(mockResults);

            // When
            QueryPreviewResponse result = queryConfigService.previewQuery(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.isValid()).isTrue();
            assertThat(result.getFinalQuery()).contains("WHERE active = true");
            assertThat(result.getFinalQuery()).contains("AND age > 18");
            assertThat(result.getFinalQuery()).contains("ORDER BY name ASC");
        }

        @Test
        @DisplayName("Should preview query with parameters")
        void shouldPreviewQueryWithParameters() {
            // Given
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("name", "John");
            parameters.put("age", 25);

            QueryPreviewRequest request = QueryPreviewRequest.builder()
                    .baseQuery("SELECT * FROM customer WHERE name = :name AND age = :age")
                    .parameters(parameters)
                    .build();

            List<Object> mockResults = Collections.singletonList("result");

            when(entityManager.createNativeQuery(anyString())).thenReturn(query);
            when(query.setParameter(anyString(), any())).thenReturn(query);
            when(query.getResultList()).thenReturn(mockResults);

            // When
            QueryPreviewResponse result = queryConfigService.previewQuery(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.isValid()).isTrue();
            verify(query, atLeast(2)).setParameter(anyString(), any());
        }

        @Test
        @DisplayName("Should handle query preview error gracefully")
        void shouldHandleQueryPreviewError() {
            // Given
            QueryPreviewRequest request = QueryPreviewRequest.builder()
                    .baseQuery("INVALID SQL QUERY")
                    .build();

            when(entityManager.createNativeQuery(anyString()))
                    .thenThrow(new RuntimeException("SQL syntax error"));

            // When
            QueryPreviewResponse result = queryConfigService.previewQuery(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.isValid()).isFalse();
            assertThat(result.getErrorMessage()).isEqualTo("SQL syntax error");
            assertThat(result.getPreviewResults()).isEmpty();
            assertThat(result.getTotalResults()).isZero();
        }

        @Test
        @DisplayName("Should limit preview results to 10")
        void shouldLimitPreviewResults() {
            // Given
            QueryPreviewRequest request = QueryPreviewRequest.builder()
                    .baseQuery("SELECT * FROM customer")
                    .build();

            List<Object> mockResults = Arrays.asList(
                    "r1", "r2", "r3", "r4", "r5", "r6", "r7", "r8", "r9", "r10"
            );

            when(entityManager.createNativeQuery(anyString())).thenReturn(query);
            when(query.getResultList()).thenReturn(mockResults);

            // When
            queryConfigService.previewQuery(request);

            // Then
            verify(query).setMaxResults(10);
        }
    }

    // Helper methods for creating test entities and DTOs
    private QueryBase createQueryBaseEntity(String nameUnique, String context) {
        return QueryBase.builder()
                .id(testId)
                .nameUnique(nameUnique)
                .context(context)
                .query("SELECT * FROM test")
                .description("Test description")
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    private QueryBaseDTO createQueryBaseDTO(String nameUnique, String context, Boolean active) {
        return QueryBaseDTO.builder()
                .nameUnique(nameUnique)
                .context(context)
                .query("SELECT * FROM test")
                .description("Test description")
                .active(active)
                .build();
    }

    private QueryFilter createQueryFilterEntity(String nameUnique, String context) {
        return QueryFilter.builder()
                .id(testId)
                .nameUnique(nameUnique)
                .context(context)
                .queryFragment("WHERE active = true")
                .description("Test filter")
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    private QueryFilterDTO createQueryFilterDTO(String nameUnique, String context, Boolean active) {
        return QueryFilterDTO.builder()
                .nameUnique(nameUnique)
                .context(context)
                .queryFragment("WHERE active = true")
                .description("Test filter")
                .active(active)
                .build();
    }

    private QueryOrder createQueryOrderEntity(String nameUnique, String context) {
        return QueryOrder.builder()
                .id(testId)
                .nameUnique(nameUnique)
                .context(context)
                .queryFragment("ORDER BY name ASC")
                .description("Test order")
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    private QueryOrderDTO createQueryOrderDTO(String nameUnique, String context, Boolean active) {
        return QueryOrderDTO.builder()
                .nameUnique(nameUnique)
                .context(context)
                .queryFragment("ORDER BY name ASC")
                .description("Test order")
                .active(active)
                .build();
    }
}

