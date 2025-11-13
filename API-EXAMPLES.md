# 📚 API Examples - POC Dynamic Query

Este documento contém exemplos práticos de uso da API do sistema de queries dinâmicas.

## 📋 Índice

1. [Query Base Examples](#query-base-examples)
2. [Query Filter Examples](#query-filter-examples)
3. [Query Order Examples](#query-order-examples)
4. [Query Preview Examples](#query-preview-examples)
5. [Use Cases Completos](#use-cases-completos)

---

## Query Base Examples

### Criar uma Query Base

**Request:**
```bash
curl -X POST http://localhost:8080/api/admin/queries/bases \
  -H "Content-Type: application/json" \
  -d '{
    "nameUnique": "customer_search",
    "context": "customer",
    "query": "SELECT id, name, email, phone, created_at FROM customers WHERE deleted_at IS NULL {{filters}} {{orders}}",
    "description": "Base query for searching customers with dynamic filters and ordering",
    "active": true
  }'
```

**Response:**
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "nameUnique": "customer_search",
  "context": "customer",
  "query": "SELECT id, name, email, phone, created_at FROM customers WHERE deleted_at IS NULL {{filters}} {{orders}}",
  "description": "Base query for searching customers with dynamic filters and ordering",
  "active": true,
  "createdAt": "2025-11-13T10:30:00",
  "updatedAt": "2025-11-13T10:30:00"
}
```

### Listar todas as Query Bases

**Request:**
```bash
curl -X GET http://localhost:8080/api/admin/queries/bases
```

**Response:**
```json
[
  {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "nameUnique": "customer_search",
    "context": "customer",
    "query": "SELECT id, name, email, phone, created_at FROM customers WHERE deleted_at IS NULL {{filters}} {{orders}}",
    "description": "Base query for searching customers",
    "active": true,
    "createdAt": "2025-11-13T10:30:00",
    "updatedAt": "2025-11-13T10:30:00"
  }
]
```

### Atualizar uma Query Base

**Request:**
```bash
curl -X PUT http://localhost:8080/api/admin/queries/bases/123e4567-e89b-12d3-a456-426614174000 \
  -H "Content-Type: application/json" \
  -d '{
    "nameUnique": "customer_search",
    "context": "customer",
    "query": "SELECT id, name, email, phone, status, created_at FROM customers WHERE deleted_at IS NULL {{filters}} {{orders}}",
    "description": "Updated: now includes status field",
    "active": true
  }'
```

### Deletar uma Query Base

**Request:**
```bash
curl -X DELETE http://localhost:8080/api/admin/queries/bases/123e4567-e89b-12d3-a456-426614174000
```

---

## Query Filter Examples

### Criar Filtros Comuns

#### Filtro de Nome (busca parcial)
**Request:**
```bash
curl -X POST http://localhost:8080/api/admin/queries/filters \
  -H "Content-Type: application/json" \
  -d '{
    "nameUnique": "filter_customer_by_name",
    "context": "customer",
    "queryFragment": "AND name LIKE :name",
    "description": "Filter customers by name using LIKE (partial match)",
    "active": true
  }'
```

#### Filtro de Email (busca exata)
**Request:**
```bash
curl -X POST http://localhost:8080/api/admin/queries/filters \
  -H "Content-Type: application/json" \
  -d '{
    "nameUnique": "filter_customer_by_email",
    "context": "customer",
    "queryFragment": "AND email = :email",
    "description": "Filter customers by exact email match",
    "active": true
  }'
```

#### Filtro de Status Ativo
**Request:**
```bash
curl -X POST http://localhost:8080/api/admin/queries/filters \
  -H "Content-Type: application/json" \
  -d '{
    "nameUnique": "filter_customer_active_only",
    "context": "customer",
    "queryFragment": "AND active = true",
    "description": "Filter only active customers",
    "active": true
  }'
```

#### Filtro de Período de Criação
**Request:**
```bash
curl -X POST http://localhost:8080/api/admin/queries/filters \
  -H "Content-Type: application/json" \
  -d '{
    "nameUnique": "filter_customer_by_date_range",
    "context": "customer",
    "queryFragment": "AND created_at BETWEEN :startDate AND :endDate",
    "description": "Filter customers created within a date range",
    "active": true
  }'
```

#### Filtro por Telefone (verificar se existe)
**Request:**
```bash
curl -X POST http://localhost:8080/api/admin/queries/filters \
  -H "Content-Type: application/json" \
  -d '{
    "nameUnique": "filter_customer_with_phone",
    "context": "customer",
    "queryFragment": "AND phone IS NOT NULL AND phone != '\'''\''",
    "description": "Filter customers that have a phone number",
    "active": true
  }'
```

### Listar todos os Filtros

**Request:**
```bash
curl -X GET http://localhost:8080/api/admin/queries/filters
```

---

## Query Order Examples

### Criar Ordenações Comuns

#### Ordenar por Nome (A-Z)
**Request:**
```bash
curl -X POST http://localhost:8080/api/admin/queries/orders \
  -H "Content-Type: application/json" \
  -d '{
    "nameUnique": "order_customer_by_name_asc",
    "context": "customer",
    "queryFragment": "ORDER BY name ASC",
    "description": "Order customers alphabetically by name (A-Z)",
    "active": true
  }'
```

#### Ordenar por Data de Criação (Mais Recente)
**Request:**
```bash
curl -X POST http://localhost:8080/api/admin/queries/orders \
  -H "Content-Type: application/json" \
  -d '{
    "nameUnique": "order_customer_by_date_desc",
    "context": "customer",
    "queryFragment": "ORDER BY created_at DESC",
    "description": "Order customers by creation date (newest first)",
    "active": true
  }'
```

#### Ordenação Múltipla
**Request:**
```bash
curl -X POST http://localhost:8080/api/admin/queries/orders \
  -H "Content-Type: application/json" \
  -d '{
    "nameUnique": "order_customer_by_status_and_name",
    "context": "customer",
    "queryFragment": "ORDER BY active DESC, name ASC",
    "description": "Order by active status first, then by name",
    "active": true
  }'
```

---

## Query Preview Examples

### Preview Simples

**Request:**
```bash
curl -X POST http://localhost:8080/api/admin/queries/preview \
  -H "Content-Type: application/json" \
  -d '{
    "baseQuery": "SELECT id, name, email FROM customers WHERE deleted_at IS NULL {{filters}} {{orders}}",
    "filters": [],
    "orders": ["ORDER BY name ASC"],
    "parameters": {}
  }'
```

**Response:**
```json
{
  "finalQuery": "SELECT id, name, email FROM customers WHERE deleted_at IS NULL  ORDER BY name ASC",
  "valid": true,
  "errorMessage": null,
  "previewResults": [
    {
      "column_1": 1,
      "column_2": "Alice Johnson",
      "column_3": "alice@example.com"
    },
    {
      "column_1": 2,
      "column_2": "Bob Smith",
      "column_3": "bob@example.com"
    }
  ],
  "totalResults": 10
}
```

### Preview com Filtro de Nome

**Request:**
```bash
curl -X POST http://localhost:8080/api/admin/queries/preview \
  -H "Content-Type: application/json" \
  -d '{
    "baseQuery": "SELECT id, name, email FROM customers WHERE deleted_at IS NULL {{filters}} {{orders}}",
    "filters": ["AND name LIKE :name"],
    "orders": ["ORDER BY name ASC"],
    "parameters": {
      "name": "%John%"
    }
  }'
```

**Response:**
```json
{
  "finalQuery": "SELECT id, name, email FROM customers WHERE deleted_at IS NULL AND name LIKE :name ORDER BY name ASC",
  "valid": true,
  "errorMessage": null,
  "previewResults": [
    {
      "column_1": 5,
      "column_2": "John Doe",
      "column_3": "john@example.com"
    },
    {
      "column_1": 12,
      "column_2": "Johnny Walker",
      "column_3": "johnny@example.com"
    }
  ],
  "totalResults": 2
}
```

### Preview com Múltiplos Filtros

**Request:**
```bash
curl -X POST http://localhost:8080/api/admin/queries/preview \
  -H "Content-Type: application/json" \
  -d '{
    "baseQuery": "SELECT id, name, email, created_at FROM customers WHERE deleted_at IS NULL {{filters}} {{orders}}",
    "filters": [
      "AND name LIKE :name",
      "AND created_at >= :startDate"
    ],
    "orders": ["ORDER BY created_at DESC"],
    "parameters": {
      "name": "%a%",
      "startDate": "2024-01-01"
    }
  }'
```

**Response:**
```json
{
  "finalQuery": "SELECT id, name, email, created_at FROM customers WHERE deleted_at IS NULL AND name LIKE :name AND created_at >= :startDate ORDER BY created_at DESC",
  "valid": true,
  "errorMessage": null,
  "previewResults": [
    {
      "column_1": 15,
      "column_2": "Sarah Connor",
      "column_3": "sarah@example.com",
      "column_4": "2024-11-10T14:30:00"
    }
  ],
  "totalResults": 8
}
```

### Preview com Query Inválida

**Request:**
```bash
curl -X POST http://localhost:8080/api/admin/queries/preview \
  -H "Content-Type: application/json" \
  -d '{
    "baseQuery": "SELECT * FROM non_existent_table {{filters}}",
    "filters": [],
    "orders": [],
    "parameters": {}
  }'
```

**Response:**
```json
{
  "finalQuery": "SELECT * FROM non_existent_table ",
  "valid": false,
  "errorMessage": "Table 'non_existent_table' doesn't exist",
  "previewResults": [],
  "totalResults": 0
}
```

---

## Use Cases Completos

### Use Case 1: Sistema de Busca de Clientes

#### Passo 1: Criar Query Base
```bash
curl -X POST http://localhost:8080/api/admin/queries/bases \
  -H "Content-Type: application/json" \
  -d '{
    "nameUnique": "customer_advanced_search",
    "context": "customer",
    "query": "SELECT c.id, c.name, c.email, c.phone, c.active, c.created_at FROM customers c WHERE c.deleted_at IS NULL {{filters}} {{orders}}",
    "description": "Advanced customer search with multiple filter options",
    "active": true
  }'
```

#### Passo 2: Criar Filtros
```bash
# Filtro por nome
curl -X POST http://localhost:8080/api/admin/queries/filters \
  -H "Content-Type: application/json" \
  -d '{
    "nameUnique": "customer_name_filter",
    "context": "customer",
    "queryFragment": "AND c.name LIKE :name",
    "description": "Search by customer name",
    "active": true
  }'

# Filtro por email
curl -X POST http://localhost:8080/api/admin/queries/filters \
  -H "Content-Type: application/json" \
  -d '{
    "nameUnique": "customer_email_filter",
    "context": "customer",
    "queryFragment": "AND c.email LIKE :email",
    "description": "Search by customer email",
    "active": true
  }'

# Filtro por status ativo
curl -X POST http://localhost:8080/api/admin/queries/filters \
  -H "Content-Type: application/json" \
  -d '{
    "nameUnique": "customer_active_filter",
    "context": "customer",
    "queryFragment": "AND c.active = :active",
    "description": "Filter by active status",
    "active": true
  }'
```

#### Passo 3: Criar Ordenações
```bash
# Ordenar por nome
curl -X POST http://localhost:8080/api/admin/queries/orders \
  -H "Content-Type: application/json" \
  -d '{
    "nameUnique": "customer_order_by_name",
    "context": "customer",
    "queryFragment": "ORDER BY c.name ASC",
    "description": "Order by name A-Z",
    "active": true
  }'

# Ordenar por data
curl -X POST http://localhost:8080/api/admin/queries/orders \
  -H "Content-Type: application/json" \
  -d '{
    "nameUnique": "customer_order_by_date",
    "context": "customer",
    "queryFragment": "ORDER BY c.created_at DESC",
    "description": "Order by creation date (newest first)",
    "active": true
  }'
```

#### Passo 4: Testar Preview
```bash
curl -X POST http://localhost:8080/api/admin/queries/preview \
  -H "Content-Type: application/json" \
  -d '{
    "baseQuery": "SELECT c.id, c.name, c.email, c.phone, c.active, c.created_at FROM customers c WHERE c.deleted_at IS NULL {{filters}} {{orders}}",
    "filters": [
      "AND c.name LIKE :name",
      "AND c.active = :active"
    ],
    "orders": ["ORDER BY c.created_at DESC"],
    "parameters": {
      "name": "%a%",
      "active": true
    }
  }'
```

### Use Case 2: Relatório de Clientes por Período

#### Criar Query Base para Relatório
```bash
curl -X POST http://localhost:8080/api/admin/queries/bases \
  -H "Content-Type: application/json" \
  -d '{
    "nameUnique": "customer_report_by_period",
    "context": "report",
    "query": "SELECT DATE(c.created_at) as date, COUNT(*) as total, COUNT(CASE WHEN c.active THEN 1 END) as active FROM customers c WHERE c.deleted_at IS NULL {{filters}} GROUP BY DATE(c.created_at) {{orders}}",
    "description": "Report of customers registered by date",
    "active": true
  }'
```

#### Criar Filtro de Período
```bash
curl -X POST http://localhost:8080/api/admin/queries/filters \
  -H "Content-Type: application/json" \
  -d '{
    "nameUnique": "report_date_range_filter",
    "context": "report",
    "queryFragment": "AND c.created_at BETWEEN :startDate AND :endDate",
    "description": "Filter report by date range",
    "active": true
  }'
```

#### Testar Relatório
```bash
curl -X POST http://localhost:8080/api/admin/queries/preview \
  -H "Content-Type: application/json" \
  -d '{
    "baseQuery": "SELECT DATE(c.created_at) as date, COUNT(*) as total, COUNT(CASE WHEN c.active THEN 1 END) as active FROM customers c WHERE c.deleted_at IS NULL {{filters}} GROUP BY DATE(c.created_at) {{orders}}",
    "filters": ["AND c.created_at BETWEEN :startDate AND :endDate"],
    "orders": ["ORDER BY date DESC"],
    "parameters": {
      "startDate": "2024-01-01",
      "endDate": "2024-12-31"
    }
  }'
```

### Use Case 3: Busca com Múltiplos Critérios Opcionais

#### Preview com Diferentes Combinações

**Apenas Nome:**
```bash
curl -X POST http://localhost:8080/api/admin/queries/preview \
  -H "Content-Type: application/json" \
  -d '{
    "baseQuery": "SELECT * FROM customers WHERE deleted_at IS NULL {{filters}} {{orders}}",
    "filters": ["AND name LIKE :name"],
    "orders": ["ORDER BY name ASC"],
    "parameters": {"name": "%John%"}
  }'
```

**Nome + Email:**
```bash
curl -X POST http://localhost:8080/api/admin/queries/preview \
  -H "Content-Type: application/json" \
  -d '{
    "baseQuery": "SELECT * FROM customers WHERE deleted_at IS NULL {{filters}} {{orders}}",
    "filters": [
      "AND name LIKE :name",
      "AND email LIKE :email"
    ],
    "orders": ["ORDER BY name ASC"],
    "parameters": {
      "name": "%John%",
      "email": "%@gmail.com"
    }
  }'
```

**Nome + Email + Data:**
```bash
curl -X POST http://localhost:8080/api/admin/queries/preview \
  -H "Content-Type: application/json" \
  -d '{
    "baseQuery": "SELECT * FROM customers WHERE deleted_at IS NULL {{filters}} {{orders}}",
    "filters": [
      "AND name LIKE :name",
      "AND email LIKE :email",
      "AND created_at >= :startDate"
    ],
    "orders": ["ORDER BY created_at DESC"],
    "parameters": {
      "name": "%John%",
      "email": "%@gmail.com",
      "startDate": "2024-01-01"
    }
  }'
```

---

## 💡 Dicas Importantes

### 1. Sempre use parâmetros nomeados
```sql
✅ Correto: AND name LIKE :name
❌ Errado:  AND name LIKE '%John%'
```

### 2. Inicie filtros com operadores lógicos
```sql
✅ Correto: AND email = :email
❌ Errado:  email = :email
```

### 3. Teste sempre no Preview antes de usar
```bash
# Sempre execute um preview para verificar a query final
curl -X POST http://localhost:8080/api/admin/queries/preview ...
```

### 4. Use contextos para organizar
```
customer - queries relacionadas a clientes
order    - queries relacionadas a pedidos
product  - queries relacionadas a produtos
report   - queries de relatórios
```

### 5. Mantenha os filtros atômicos
```sql
✅ Bom:  Um filtro = Um critério
   "AND name LIKE :name"
   
❌ Ruim: Múltiplos critérios em um filtro
   "AND (name LIKE :name OR email LIKE :email)"
```

---

## 🔧 Troubleshooting

### Erro: "Query is invalid"
- Verifique a sintaxe SQL no preview
- Certifique-se de que todos os parâmetros foram fornecidos
- Valide se a tabela e colunas existem

### Erro: "Parameter not found"
- Verifique se todos os `:paramName` têm valores correspondentes no objeto `parameters`
- Certifique-se de usar dois pontos antes do nome do parâmetro

### Query retorna vazio
- Verifique se os valores dos parâmetros estão corretos
- Para LIKE, lembre-se de adicionar `%` nos valores: `"%John%"`
- Teste a query no preview para ver a SQL final

### CORS Error
- Verifique se o `@CrossOrigin` está configurado no controller
- Certifique-se de que a porta está correta (8080)

---

**Última atualização:** 2025-11-13

