package com.example.poc.query.dynamic.usecase;

import com.example.poc.query.dynamic.dto.CustomerDTO;
import com.example.poc.query.dynamic.dto.CustomerFilterDTO;
import com.example.poc.query.dynamic.dto.CustomerSearchResponseDTO;
import com.example.poc.query.dynamic.entity.Customer;
import com.example.poc.query.dynamic.repository.CustomerRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("SearchCustomerUseCase Integration Tests")
class SearchCustomerUseCaseIntegrationTest {

    @Autowired
    private SearchCustomerUseCase searchCustomerUseCase;

    @Autowired
    private CustomerRepository customerRepository;


    @BeforeEach
    void setUp() {
        // Limpar base antes de cada teste
        customerRepository.deleteAll();

        // Criar dados de teste
        customerRepository.save(Customer.builder()
                .name("Empresa Silva LTDA")
                .tradeName("Silva")
                .cnpj("11111111111111")
                .active(true)
                .blocked(false)
                .createdAt(LocalDateTime.now().minusDays(3))
                .updatedAt(LocalDateTime.now().minusDays(3))
                .build());

        customerRepository.save(Customer.builder()
                .name("Empresa Santos ME")
                .tradeName("Santos")
                .cnpj("22222222222222")
                .active(true)
                .blocked(true)
                .createdAt(LocalDateTime.now().minusDays(2))
                .updatedAt(LocalDateTime.now().minusDays(2))
                .build());

        customerRepository.save(Customer.builder()
                .name("Empresa Oliveira S/A")
                .tradeName("Oliveira")
                .cnpj("33333333333333")
                .active(false)
                .blocked(false)
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build());

        customerRepository.save(Customer.builder()
                .name("Silva Comércio EIRELI")
                .tradeName("Silva Comércio")
                .cnpj("44444444444444")
                .active(true)
                .blocked(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
    }

    @AfterEach
    void tearDown() {
        // Limpar base após cada teste para garantir isolamento
        customerRepository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("Deve retornar todos os customers sem filtros")
    void shouldReturnAllCustomersWithoutFilters() {
        // Given
        CustomerFilterDTO filter = CustomerFilterDTO.builder()
                .filters(List.of())
                .parameters(Map.of())
                .build();
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<CustomerDTO> result = searchCustomerUseCase.execute(filter, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(4);
        assertThat(result.getTotalElements()).isEqualTo(4);
    }

    @Test
    @Order(2)
    @DisplayName("Deve filtrar apenas customers ativos usando filtro dinâmico")
    void shouldFilterOnlyActiveCustomers() {
        // Given
        CustomerFilterDTO filter = CustomerFilterDTO.builder()
                .filters(List.of("only_customer_actives"))
                .parameters(Map.of())
                .build();
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<CustomerDTO> result = searchCustomerUseCase.execute(filter, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent())
                .allMatch(CustomerDTO::getActive)
                .extracting(CustomerDTO::getName)
                .containsExactlyInAnyOrder(
                        "Empresa Silva LTDA",
                        "Empresa Santos ME",
                        "Silva Comércio EIRELI"
                );
    }

    @Test
    @Order(3)
    @DisplayName("Deve filtrar apenas customers bloqueados usando filtro dinâmico")
    void shouldFilterOnlyBlockedCustomers() {
        // Given
        CustomerFilterDTO filter = CustomerFilterDTO.builder()
                .filters(List.of("only_customer_blocked"))
                .parameters(Map.of())
                .build();
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<CustomerDTO> result = searchCustomerUseCase.execute(filter, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Empresa Santos ME");
        assertThat(result.getContent().get(0).getBlocked()).isTrue();
    }

    @Test
    @Order(4)
    @DisplayName("Deve filtrar customer por nome usando filtro dinâmico")
    void shouldFilterCustomerByName() {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", "Silva");

        CustomerFilterDTO filter = CustomerFilterDTO.builder()
                .filters(List.of("customer_by_name"))
                .parameters(parameters)
                .build();
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<CustomerDTO> result = searchCustomerUseCase.execute(filter, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
                .extracting(CustomerDTO::getName)
                .containsExactlyInAnyOrder(
                        "Empresa Silva LTDA",
                        "Silva Comércio EIRELI"
                );
    }

    @Test
    @Order(5)
    @DisplayName("Deve filtrar customer por CNPJ usando filtro dinâmico")
    void shouldFilterCustomerByCnpj() {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("cnpj", "22222222222222");

        CustomerFilterDTO filter = CustomerFilterDTO.builder()
                .filters(List.of("customer_by_cnpj"))
                .parameters(parameters)
                .build();
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<CustomerDTO> result = searchCustomerUseCase.execute(filter, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Empresa Santos ME");
        assertThat(result.getContent().get(0).getCnpj()).isEqualTo("22222222222222");
    }

    @Test
    @Order(6)
    @DisplayName("Deve combinar múltiplos filtros dinâmicos")
    void shouldCombineMultipleFilters() {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", "Silva");

        CustomerFilterDTO filter = CustomerFilterDTO.builder()
                .filters(List.of("only_customer_actives", "customer_by_name"))
                .parameters(parameters)
                .build();
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<CustomerDTO> result = searchCustomerUseCase.execute(filter, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
                .allMatch(CustomerDTO::getActive)
                .extracting(CustomerDTO::getName)
                .containsExactlyInAnyOrder(
                        "Empresa Silva LTDA",
                        "Silva Comércio EIRELI"
                );
    }

    @Test
    @Order(7)
    @DisplayName("Deve ordenar por nome ascendente usando ordenação dinâmica")
    void shouldOrderByNameAscending() {
        // Given
        CustomerFilterDTO filter = CustomerFilterDTO.builder()
                .filters(List.of())
                .order("customer_order_by_name_asc")
                .parameters(Map.of())
                .build();
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<CustomerDTO> result = searchCustomerUseCase.execute(filter, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(4);
        assertThat(result.getContent())
                .extracting(CustomerDTO::getName)
                .containsExactly(
                        "Empresa Oliveira S/A",
                        "Empresa Santos ME",
                        "Empresa Silva LTDA",
                        "Silva Comércio EIRELI"
                );
    }

    @Test
    @Order(8)
    @DisplayName("Deve ordenar por nome descendente usando ordenação dinâmica")
    void shouldOrderByNameDescending() {
        // Given
        CustomerFilterDTO filter = CustomerFilterDTO.builder()
                .filters(List.of())
                .order("customer_order_by_name_desc")
                .parameters(Map.of())
                .build();
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<CustomerDTO> result = searchCustomerUseCase.execute(filter, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(4);
        assertThat(result.getContent())
                .extracting(CustomerDTO::getName)
                .containsExactly(
                        "Silva Comércio EIRELI",
                        "Empresa Silva LTDA",
                        "Empresa Santos ME",
                        "Empresa Oliveira S/A"
                );
    }

    @Test
    @Order(9)
    @DisplayName("Deve ordenar por data de criação descendente usando ordenação dinâmica")
    void shouldOrderByCreatedAtDescending() {
        // Given
        CustomerFilterDTO filter = CustomerFilterDTO.builder()
                .filters(List.of())
                .order("customer_order_by_created_desc")
                .parameters(Map.of())
                .build();
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<CustomerDTO> result = searchCustomerUseCase.execute(filter, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(4);
        assertThat(result.getContent())
                .extracting(CustomerDTO::getName)
                .containsExactly(
                        "Silva Comércio EIRELI",      // Mais recente
                        "Empresa Oliveira S/A",
                        "Empresa Santos ME",
                        "Empresa Silva LTDA"          // Mais antigo
                );
    }

    @Test
    @Order(10)
    @DisplayName("Deve combinar filtros e ordenação dinâmicos")
    void shouldCombineFiltersAndOrdering() {
        // Given
        CustomerFilterDTO filter = CustomerFilterDTO.builder()
                .filters(List.of("only_customer_actives"))
                .order("customer_order_by_name_desc")
                .parameters(Map.of())
                .build();
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<CustomerDTO> result = searchCustomerUseCase.execute(filter, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent())
                .allMatch(CustomerDTO::getActive)
                .extracting(CustomerDTO::getName)
                .containsExactly(
                        "Silva Comércio EIRELI",
                        "Empresa Silva LTDA",
                        "Empresa Santos ME"
                );
    }

    @Test
    @Order(11)
    @DisplayName("Deve aplicar paginação corretamente")
    void shouldApplyPaginationCorrectly() {
        // Given
        CustomerFilterDTO filter = CustomerFilterDTO.builder()
                .filters(List.of())
                .order("customer_order_by_name_asc")
                .parameters(Map.of())
                .build();
        Pageable pageable = PageRequest.of(0, 2);

        // When
        Page<CustomerDTO> result = searchCustomerUseCase.execute(filter, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(4);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getContent())
                .extracting(CustomerDTO::getName)
                .containsExactly(
                        "Empresa Oliveira S/A",
                        "Empresa Santos ME"
                );
    }

    @Test
    @Order(12)
    @DisplayName("Deve retornar segunda página corretamente")
    void shouldReturnSecondPageCorrectly() {
        // Given
        CustomerFilterDTO filter = CustomerFilterDTO.builder()
                .filters(List.of())
                .order("customer_order_by_name_asc")
                .parameters(Map.of())
                .build();
        Pageable pageable = PageRequest.of(1, 2);

        // When
        Page<CustomerDTO> result = searchCustomerUseCase.execute(filter, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(4);
        assertThat(result.getNumber()).isEqualTo(1);
        assertThat(result.getContent())
                .extracting(CustomerDTO::getName)
                .containsExactly(
                        "Empresa Silva LTDA",
                        "Silva Comércio EIRELI"
                );
    }

    @Test
    @Order(13)
    @DisplayName("Deve retornar dados e opções disponíveis juntos")
    void shouldReturnDataAndAvailableOptionsToghether() {
        // Given
        CustomerFilterDTO filter = CustomerFilterDTO.builder()
                .filters(List.of("only_customer_actives"))
                .order("customer_order_by_name_asc")
                .parameters(Map.of())
                .build();
        Pageable pageable = PageRequest.of(0, 10);

        // When
        CustomerSearchResponseDTO response = searchCustomerUseCase.executeWithOptions(filter, pageable);

        // Then
        assertThat(response).isNotNull();

        // Validar dados
        assertThat(response.getCustomers()).isNotNull();
        assertThat(response.getCustomers().getContent()).hasSize(3);

        // Validar opções disponíveis
        assertThat(response.getAvailableQueries()).isNotNull();
        assertThat(response.getAvailableQueries().getContext()).isEqualTo("CUSTOMER");

        // Validar filtros disponíveis
        assertThat(response.getAvailableQueries().getFilters())
                .isNotEmpty()
                .extracting("nameUnique")
                .contains(
                        "only_customer_actives",
                        "only_customer_blocked",
                        "customer_by_cnpj",
                        "customer_by_name",
                        "customer_by_id"
                );

        // Validar ordenações disponíveis
        assertThat(response.getAvailableQueries().getOrders())
                .isNotEmpty()
                .extracting("nameUnique")
                .contains(
                        "customer_order_by_name_asc",
                        "customer_order_by_name_desc",
                        "customer_order_by_created_desc",
                        "customer_order_by_created_asc"
                );

        // Validar que não expõe queryFragment
        assertThat(response.getAvailableQueries().getFilters())
                .allMatch(filter1 -> filter1.getNameUnique() != null)
                .allMatch(filter1 -> filter1.getDescription() != null);
    }

    @Test
    @Order(14)
    @DisplayName("Deve lançar exceção quando filtro não existe")
    void shouldThrowExceptionWhenFilterDoesNotExist() {
        // Given
        CustomerFilterDTO filter = CustomerFilterDTO.builder()
                .filters(List.of("filtro_que_nao_existe"))
                .parameters(Map.of())
                .build();
        Pageable pageable = PageRequest.of(0, 10);

        // When / Then
        assertThatThrownBy(() -> searchCustomerUseCase.execute(filter, pageable))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Filter not found");
    }

    @Test
    @Order(15)
    @DisplayName("Deve lançar exceção quando ordenação não existe")
    void shouldThrowExceptionWhenOrderDoesNotExist() {
        // Given
        CustomerFilterDTO filter = CustomerFilterDTO.builder()
                .filters(List.of())
                .order("ordem_que_nao_existe")
                .parameters(Map.of())
                .build();
        Pageable pageable = PageRequest.of(0, 10);

        // When / Then
        assertThatThrownBy(() -> searchCustomerUseCase.execute(filter, pageable))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Order not found");
    }

    @Test
    @Order(16)
    @DisplayName("Deve filtrar customer por ID usando filtro dinâmico")
    void shouldFilterCustomerById() {
        // Given
        Customer savedCustomer = customerRepository.findAll().get(0);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", savedCustomer.getId().toString());

        CustomerFilterDTO filter = CustomerFilterDTO.builder()
                .filters(List.of("customer_by_id"))
                .parameters(parameters)
                .build();
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<CustomerDTO> result = searchCustomerUseCase.execute(filter, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(savedCustomer.getId());
        assertThat(result.getContent().get(0).getName()).isEqualTo(savedCustomer.getName());
    }

    @Test
    @Order(17)
    @DisplayName("Deve ordenar por data de criação ascendente usando ordenação dinâmica")
    void shouldOrderByCreatedAtAscending() {
        // Given
        CustomerFilterDTO filter = CustomerFilterDTO.builder()
                .filters(List.of())
                .order("customer_order_by_created_asc")
                .parameters(Map.of())
                .build();
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<CustomerDTO> result = searchCustomerUseCase.execute(filter, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(4);
        assertThat(result.getContent())
                .extracting(CustomerDTO::getName)
                .containsExactly(
                        "Empresa Silva LTDA",      // Mais antigo
                        "Empresa Santos ME",
                        "Empresa Oliveira S/A",
                        "Silva Comércio EIRELI"    // Mais recente
                );
    }

    @Test
    @Order(18)
    @DisplayName("Deve ignorar customers com deletedAt preenchido (soft delete)")
    void shouldIgnoreSoftDeletedCustomers() {
        // Given - Criar customer e marcar como deletado
        customerRepository.save(Customer.builder()
                .name("Empresa Deletada LTDA")
                .tradeName("Deletada")
                .cnpj("99999999999999")
                .active(true)
                .blocked(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deletedAt(LocalDateTime.now())
                .build());

        CustomerFilterDTO filter = CustomerFilterDTO.builder()
                .filters(List.of())
                .parameters(Map.of())
                .build();
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<CustomerDTO> result = searchCustomerUseCase.execute(filter, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(4); // Não deve incluir o deletado
        assertThat(result.getContent())
                .extracting(CustomerDTO::getName)
                .doesNotContain("Empresa Deletada LTDA");
    }

    @Test
    @Order(19)
    @DisplayName("Deve lançar exceção ao tentar filtrar por ID com formato inválido")
    void shouldThrowExceptionWhenFilteringByInvalidUUID() {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", "invalid-uuid-format");

        CustomerFilterDTO filter = CustomerFilterDTO.builder()
                .filters(List.of("customer_by_id"))
                .parameters(parameters)
                .build();
        Pageable pageable = PageRequest.of(0, 10);

        // When / Then
        assertThatThrownBy(() -> searchCustomerUseCase.execute(filter, pageable))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid UUID format");
    }

    @Test
    @Order(20)
    @DisplayName("Deve combinar filtro por ID com outros filtros")
    void shouldCombineIdFilterWithOtherFilters() {
        // Given
        Customer savedCustomer = customerRepository.findAll().stream()
                .filter(Customer::getActive)
                .findFirst()
                .orElseThrow();

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", savedCustomer.getId().toString());

        CustomerFilterDTO filter = CustomerFilterDTO.builder()
                .filters(List.of("customer_by_id", "only_customer_actives"))
                .parameters(parameters)
                .build();
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<CustomerDTO> result = searchCustomerUseCase.execute(filter, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(savedCustomer.getId());
        assertThat(result.getContent().get(0).getActive()).isTrue();
    }
}

