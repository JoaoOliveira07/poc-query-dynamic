# POC - Dynamic Query System

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-green)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)
![Liquibase](https://img.shields.io/badge/Liquibase-4.24.0-red)

## 📋 Descrição

POC (Proof of Concept) de um sistema de consultas dinâmicas que permite construir queries complexas em tempo de execução através de filtros e ordenações configuráveis no banco de dados.

Este projeto demonstra como criar um sistema flexível onde filtros e ordenações podem ser cadastrados dinamicamente no banco de dados e combinados via API, sem a necessidade de alterar código.

## 🎯 Funcionalidades

- ✅ **Queries Dinâmicas**: Construção de queries SQL em tempo de execução
- ✅ **Filtros Configuráveis**: Filtros armazenados no banco e aplicados dinamicamente
- ✅ **Ordenações Flexíveis**: Múltiplas opções de ordenação configuráveis
- ✅ **Combinação de Filtros**: Possibilidade de combinar múltiplos filtros
- ✅ **Conversão Automática de Tipos**: Sistema inteligente de conversão de parâmetros (String → UUID, etc.)
- ✅ **Paginação**: Suporte completo a paginação de resultados
- ✅ **API RESTful**: Interface REST para consultas
- ✅ **Soft Delete**: Suporte a exclusão lógica de registros

## 🚀 Tecnologias

- **Java 17**
- **Spring Boot 3.2.0**
  - Spring Data JPA
  - Spring Web
- **PostgreSQL 15**
- **Liquibase** - Versionamento de banco de dados
- **Docker & Docker Compose** - Containerização
- **Lombok** - Redução de boilerplate
- **Maven** - Gerenciamento de dependências

## 📁 Estrutura do Projeto

```
poc.query.dynamic/
├── src/main/java/com/example/poc/query/dynamic/
│   ├── config/           # Configurações (Liquibase, etc.)
│   ├── controller/       # Controllers REST
│   ├── dto/             # Data Transfer Objects
│   ├── entity/          # Entidades JPA
│   ├── repository/      # Repositories Spring Data
│   ├── service/         # Serviços de negócio
│   └── usecase/         # Use Cases (lógica de aplicação)
├── src/main/resources/
│   ├── db/changelog/    # Scripts Liquibase
│   └── application.yaml # Configuração da aplicação
├── docker-compose.yml   # Configuração Docker
└── pom.xml             # Dependências Maven
```

## 🗄️ Modelo de Dados

### Tabelas Principais

- **customer**: Clientes do sistema
- **query_base**: Queries base do sistema
- **query_filter**: Filtros aplicáveis nas queries
- **query_order**: Ordenações disponíveis

### Exemplo de Entidades

**Customer**
```java
{
  "id": "uuid",
  "name": "string",
  "tradeName": "string",
  "cnpj": "string",
  "active": boolean,
  "blocked": boolean,
  "createdAt": "datetime",
  "updatedAt": "datetime",
  "deletedAt": "datetime"
}
```

## 🔧 Configuração e Execução

### Pré-requisitos

- Java 17+
- Maven 3.8+
- Docker e Docker Compose (opcional, para desenvolvimento)

### 1. Clonar o Repositório

```bash
git clone https://github.com/seu-usuario/poc.query.dynamic.git
cd poc.query.dynamic
```

### 2. Executar com Docker

```bash
# Subir o banco de dados
docker-compose up -d

# Executar a aplicação
mvn spring-boot:run
```

### 3. Executar sem Docker

Configure o PostgreSQL manualmente e ajuste o `application.yaml` com suas credenciais:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/dynamic_query_db
    username: seu_usuario
    password: sua_senha
```

Depois execute:

```bash
mvn clean install
mvn spring-boot:run
```

## 📡 API Endpoints

### 1. Buscar Customers com Filtros Dinâmicos

**POST** `/api/customers/search`

**Request Body:**
```json
{
  "filters": ["only_customer_actives", "customer_by_name"],
  "order": "customer_order_by_name_asc",
  "parameters": {
    "name": "Silva"
  }
}
```

**Parâmetros de Query (opcionais):**
- `page`: Número da página (default: 0)
- `size`: Tamanho da página (default: 20)

**Response:**
```json
{
  "customers": {
    "content": [...],
    "totalElements": 5,
    "totalPages": 1,
    "size": 20,
    "number": 0
  },
  "availableQueries": {
    "context": "CUSTOMER",
    "filters": [...],
    "orders": [...]
  }
}
```

### Filtros Disponíveis

| Nome do Filtro | Descrição | Parâmetros |
|---------------|-----------|------------|
| `only_customer_actives` | Apenas clientes ativos | - |
| `only_customer_blocked` | Apenas clientes bloqueados | - |
| `customer_by_name` | Filtrar por nome (match parcial) | `name` |
| `customer_by_cnpj` | Filtrar por CNPJ (match exato) | `cnpj` |
| `customer_by_id` | Filtrar por ID | `id` (UUID) |

### Ordenações Disponíveis

| Nome da Ordenação | Descrição |
|------------------|-----------|
| `customer_order_by_name_asc` | Ordenar por nome (A-Z) |
| `customer_order_by_name_desc` | Ordenar por nome (Z-A) |
| `customer_order_by_created_asc` | Ordenar por data de criação (mais antigos) |
| `customer_order_by_created_desc` | Ordenar por data de criação (mais recentes) |

## 💡 Exemplos de Uso

### Exemplo 1: Buscar apenas clientes ativos

```bash
curl -X POST http://localhost:8080/api/customers/search \
  -H "Content-Type: application/json" \
  -d '{
    "filters": ["only_customer_actives"],
    "order": "customer_order_by_name_asc",
    "parameters": {}
  }'
```

### Exemplo 2: Buscar por nome com paginação

```bash
curl -X POST "http://localhost:8080/api/customers/search?page=0&size=10" \
  -H "Content-Type: application/json" \
  -d '{
    "filters": ["customer_by_name"],
    "order": "customer_order_by_name_asc",
    "parameters": {
      "name": "Silva"
    }
  }'
```

### Exemplo 3: Buscar por ID (UUID)

```bash
curl -X POST http://localhost:8080/api/customers/search \
  -H "Content-Type: application/json" \
  -d '{
    "filters": ["customer_by_id"],
    "order": null,
    "parameters": {
      "id": "650e8400-e29b-41d4-a716-446655440000"
    }
  }'
```

### Exemplo 4: Combinar múltiplos filtros

```bash
curl -X POST http://localhost:8080/api/customers/search \
  -H "Content-Type: application/json" \
  -d '{
    "filters": ["only_customer_actives", "customer_by_name"],
    "order": "customer_order_by_created_desc",
    "parameters": {
      "name": "Empresa"
    }
  }'
```

## 🏗️ Arquitetura

O sistema utiliza uma arquitetura em camadas:

1. **Controller Layer**: Recebe as requisições HTTP
2. **UseCase Layer**: Orquestra a lógica de negócio
3. **Service Layer**: Implementa a lógica de queries dinâmicas
4. **Repository Layer**: Acesso aos dados

### Fluxo de Execução

```
Request → Controller → UseCase → DynamicQueryService → Repository → Database
                                        ↓
                                Type Conversion
                                        ↓
                                Query Building
```

### Sistema de Conversão de Tipos

O `DynamicQueryService` possui um sistema inteligente de conversão de tipos:

- **String → UUID**: Automático para parâmetros do tipo `id`
- Extensível para outros tipos (datas, enums, etc.)

## 🧪 Testes

```bash
# Executar todos os testes
mvn test

# Executar testes específicos
mvn test -Dtest=SearchCustomerUseCaseIntegrationTest
```

## 📝 Liquibase

O projeto utiliza Liquibase para versionamento do banco de dados. Os changesets estão em:

```
src/main/resources/db/changelog/changes/
├── 001-create-customer-table.xml
├── 002-create-dynamic-query-tables.xml
├── 003-insert-initial-query-data.xml
├── 004-add-deleted-at-to-customer.xml
└── 005-insert-sample-customers.xml
```

## 🔜 Melhorias Futuras

- [ ] Adicionar autenticação e autorização
- [ ] Implementar cache para queries frequentes
- [ ] Adicionar validação de parâmetros obrigatórios por filtro
- [ ] Criar interface administrativa para gerenciar filtros/ordenações
- [ ] Adicionar métricas e monitoramento
- [ ] Implementar API de sugestão de filtros baseada no contexto
- [ ] Suporte a agregações (COUNT, SUM, AVG, etc.)
- [ ] Exportação de resultados (CSV, Excel)

## 📄 Licença

Este projeto é uma POC (Proof of Concept) para fins educacionais.

## 👨‍💻 Autor

Desenvolvido por [Seu Nome]

---

⭐ Se este projeto foi útil para você, considere dar uma estrela no repositório!

