# 🔍 POC Dynamic Query - Sistema de Queries Dinâmicas

## 📋 Visão Geral

Este projeto é uma Prova de Conceito (POC) para gerenciar queries SQL dinâmicas e reutilizáveis através de uma interface administrativa web. O sistema permite criar, editar e testar queries SQL de forma modular usando três componentes principais: **Query Base**, **Query Filter** e **Query Order**.

## ✨ Características Principais

- 🎯 **Interface Administrativa Moderna**: Painel web completo com tabs organizadas
- 🔍 **Query Preview**: Teste suas queries antes de salvá-las e veja resultados em tempo real
- 📦 **Componentes Reutilizáveis**: Crie filtros e ordenações que podem ser combinados
- 🎨 **UI/UX Intuitiva**: Interface limpa com documentação integrada
- 🔒 **Segurança**: Uso de parâmetros nomeados para prevenir SQL Injection
- 📊 **Visualização de Dados**: Preview de resultados direto na interface

## 🚀 Como Executar

### Pré-requisitos

- Java 17 ou superior
- Maven 3.6+
- Docker e Docker Compose (para banco de dados)

### Passos

1. **Clone o repositório**
```bash
git clone <repository-url>
cd poc-query-dynamic
```

2. **Inicie o banco de dados**
```bash
docker-compose up -d
```

3. **Execute a aplicação**
```bash
mvn spring-boot:run
```

4. **Acesse a interface administrativa**
```
http://localhost:8080
```

## 🎯 Conceitos do Sistema

### 📋 Query Base

A **Query Base** é a estrutura principal da consulta SQL. Ela define o SELECT principal e pode incluir placeholders para inserção dinâmica de filtros e ordenações.

**Características:**
- Contém a query SQL base completa
- Usa placeholders `{{filters}}` e `{{orders}}`
- Pode ser reutilizada em diferentes contextos

**Exemplo:**
```sql
SELECT id, name, email, created_at 
FROM customers 
WHERE deleted_at IS NULL {{filters}} {{orders}}
```

**Campos:**
- `nameUnique`: Identificador único (ex: "customer_search_base")
- `context`: Contexto da query (ex: "customer", "order", "product")
- `query`: A query SQL com placeholders
- `description`: Descrição do propósito da query
- `active`: Status ativo/inativo

### 🔍 Query Filter

Os **Query Filters** são fragmentos SQL reutilizáveis que representam condições de filtro. Podem ser aplicados a diferentes queries do mesmo contexto.

**Características:**
- Fragmentos SQL que começam com operadores lógicos (AND, OR)
- Usam parâmetros nomeados (`:paramName`)
- Podem ser combinados em uma mesma query

**Exemplos:**
```sql
-- Filtro por nome (busca parcial)
AND name LIKE :name

-- Filtro por email exato
AND email = :email

-- Filtro por período de criação
AND created_at >= :startDate AND created_at <= :endDate

-- Filtro por status ativo
AND active = true
```

**Campos:**
- `nameUnique`: Identificador único (ex: "filter_by_name")
- `context`: Contexto (deve coincidir com a Query Base)
- `queryFragment`: O fragmento SQL do filtro
- `description`: Descrição do que o filtro faz
- `active`: Status ativo/inativo

### 📊 Query Order

As **Query Orders** definem a ordenação dos resultados. Podem incluir múltiplas colunas e direções.

**Características:**
- Fragmentos SQL de ordenação
- Podem incluir múltiplas colunas
- Suportam ASC e DESC

**Exemplos:**
```sql
-- Ordenação simples
ORDER BY name ASC

-- Ordenação por data de criação (mais recente primeiro)
ORDER BY created_at DESC

-- Ordenação múltipla
ORDER BY status ASC, created_at DESC
```

**Campos:**
- `nameUnique`: Identificador único (ex: "order_by_name_asc")
- `context`: Contexto (deve coincidir com a Query Base)
- `queryFragment`: O fragmento SQL de ordenação
- `description`: Descrição da ordenação
- `active`: Status ativo/inativo

## 🎨 Interface Administrativa

### Tabs Disponíveis

#### 1. 📋 Query Base
- Lista todas as queries base cadastradas
- Permite criar, editar e deletar queries base
- Visualiza status (ativo/inativo)

#### 2. 🔍 Query Filter
- Gerencia todos os filtros disponíveis
- Cria e edita fragmentos de filtro
- Organiza por contexto

#### 3. 📊 Query Order
- Gerencia ordenações disponíveis
- Define fragmentos de ORDER BY
- Agrupa por contexto

#### 4. 🎯 Query Preview
- **Funcionalidade Principal**: Teste suas queries antes de usar!
- Digite a query base, adicione filtros e ordenações
- Veja a query SQL final gerada
- Execute e visualize resultados de exemplo
- Verifica erros de sintaxe em tempo real

#### 5. 📖 Documentation
- Documentação completa integrada
- Exemplos práticos
- Melhores práticas
- Guia de uso passo a passo

## 🔧 Como Usar - Exemplo Completo

### Cenário: Sistema de Busca de Clientes

#### Passo 1: Criar a Query Base

Acesse a tab **Query Base** e clique em "+ New Query Base":

```
Name Unique: customer_search
Context: customer
Query: SELECT id, name, email, phone, created_at FROM customers WHERE deleted_at IS NULL {{filters}} {{orders}}
Description: Base query for searching customers with dynamic filters
Active: ✓
```

#### Passo 2: Criar Filtros

Acesse a tab **Query Filter** e crie os seguintes filtros:

**Filtro 1 - Busca por Nome:**
```
Name Unique: filter_customer_by_name
Context: customer
Query Fragment: AND name LIKE :name
Description: Filter customers by name (partial match)
Active: ✓
```

**Filtro 2 - Busca por Email:**
```
Name Unique: filter_customer_by_email
Context: customer
Query Fragment: AND email = :email
Description: Filter customers by exact email
Active: ✓
```

**Filtro 3 - Busca por Data de Criação:**
```
Name Unique: filter_customer_by_date_range
Context: customer
Query Fragment: AND created_at BETWEEN :startDate AND :endDate
Description: Filter customers created within date range
Active: ✓
```

#### Passo 3: Criar Ordenações

Acesse a tab **Query Order** e crie:

**Ordenação 1 - Por Nome:**
```
Name Unique: order_customer_by_name
Context: customer
Query Fragment: ORDER BY name ASC
Description: Order customers alphabetically by name
Active: ✓
```

**Ordenação 2 - Por Data de Criação:**
```
Name Unique: order_customer_by_date_desc
Context: customer
Query Fragment: ORDER BY created_at DESC
Description: Order customers by creation date (newest first)
Active: ✓
```

#### Passo 4: Testar no Query Preview

Acesse a tab **Query Preview** e teste sua query:

```
Base Query:
SELECT id, name, email, phone, created_at FROM customers WHERE deleted_at IS NULL {{filters}} {{orders}}

Filter Fragments (one per line):
AND name LIKE :name
AND created_at >= :startDate

Order Fragments (one per line):
ORDER BY created_at DESC

Parameters (JSON):
{
  "name": "%John%",
  "startDate": "2024-01-01"
}
```

Clique em **"🎯 Preview Query"** e você verá:

**Query Final Gerada:**
```sql
SELECT id, name, email, phone, created_at 
FROM customers 
WHERE deleted_at IS NULL 
AND name LIKE :name 
AND created_at >= :startDate 
ORDER BY created_at DESC
```

**Status:** ✅ Query is valid!

**Preview Results:** Tabela com até 10 resultados de exemplo

## 📡 API Endpoints

### Query Base
```
GET    /api/admin/queries/bases          - Lista todas as query bases
GET    /api/admin/queries/bases/{id}     - Busca uma query base por ID
POST   /api/admin/queries/bases          - Cria uma nova query base
PUT    /api/admin/queries/bases/{id}     - Atualiza uma query base
DELETE /api/admin/queries/bases/{id}     - Deleta uma query base
```

### Query Filter
```
GET    /api/admin/queries/filters        - Lista todos os filtros
GET    /api/admin/queries/filters/{id}   - Busca um filtro por ID
POST   /api/admin/queries/filters        - Cria um novo filtro
PUT    /api/admin/queries/filters/{id}   - Atualiza um filtro
DELETE /api/admin/queries/filters/{id}   - Deleta um filtro
```

### Query Order
```
GET    /api/admin/queries/orders         - Lista todas as ordenações
GET    /api/admin/queries/orders/{id}    - Busca uma ordenação por ID
POST   /api/admin/queries/orders         - Cria uma nova ordenação
PUT    /api/admin/queries/orders/{id}    - Atualiza uma ordenação
DELETE /api/admin/queries/orders/{id}    - Deleta uma ordenação
```

### Query Preview
```
POST   /api/admin/queries/preview        - Testa e visualiza uma query
```

**Exemplo de Request:**
```json
{
  "baseQuery": "SELECT * FROM customers WHERE 1=1 {{filters}} {{orders}}",
  "filters": ["AND name LIKE :name", "AND active = true"],
  "orders": ["ORDER BY name ASC"],
  "parameters": {
    "name": "%John%"
  }
}
```

**Exemplo de Response:**
```json
{
  "finalQuery": "SELECT * FROM customers WHERE 1=1 AND name LIKE :name AND active = true ORDER BY name ASC",
  "valid": true,
  "errorMessage": null,
  "previewResults": [
    {
      "id": 1,
      "name": "John Doe",
      "email": "john@example.com"
    }
  ],
  "totalResults": 5
}
```

## 💡 Melhores Práticas

### 1. Nomenclatura
- Use nomes descritivos e únicos para `nameUnique`
- Siga um padrão: `{entity}_{action}` (ex: "customer_search", "order_by_date")
- Use `snake_case` para consistência

### 2. Contextos
- Agrupe queries relacionadas no mesmo contexto
- Exemplos: "customer", "order", "product", "invoice"
- Facilita a organização e reutilização

### 3. Parâmetros
- Sempre use parâmetros nomeados (`:paramName`)
- **NUNCA** concatene valores diretamente na query
- Previne SQL Injection e melhora a segurança

### 4. Filtros
- Sempre comece filtros com operadores lógicos (AND, OR)
- Mantenha filtros atômicos (um propósito por filtro)
- Crie filtros reutilizáveis

### 5. Testing
- **SEMPRE** teste no Query Preview antes de usar em produção
- Verifique a query final gerada
- Confirme que os parâmetros estão corretos
- Valide os resultados retornados

### 6. Documentação
- Preencha o campo `description` para todas as queries
- Explique o propósito e quando usar
- Documente parâmetros esperados

### 7. Status Ativo/Inativo
- Use o campo `active` para desabilitar temporariamente
- Não delete queries que podem ser necessárias no futuro
- Facilita rollback e debugging

## 🏗️ Estrutura do Projeto

```
src/main/java/com/example/poc/query/dynamic/
├── controller/
│   ├── CustomerController.java          # Controller de exemplo para clientes
│   └── QueryConfigController.java       # Controller de administração de queries
├── dto/
│   ├── QueryBaseDTO.java                # DTO para Query Base
│   ├── QueryFilterDTO.java              # DTO para Query Filter
│   ├── QueryOrderDTO.java               # DTO para Query Order
│   ├── QueryPreviewRequest.java         # Request para preview
│   └── QueryPreviewResponse.java        # Response do preview
├── entity/
│   ├── QueryBase.java                   # Entidade Query Base
│   ├── QueryFilter.java                 # Entidade Query Filter
│   └── QueryOrder.java                  # Entidade Query Order
├── repository/
│   ├── QueryBaseRepository.java         # Repositório Query Base
│   ├── QueryFilterRepository.java       # Repositório Query Filter
│   └── QueryOrderRepository.java        # Repositório Query Order
├── service/
│   └── QueryConfigService.java          # Service de gerenciamento de queries
└── Application.java                     # Classe principal

src/main/resources/
├── static/
│   └── index.html                       # Interface administrativa
├── db/changelog/
│   └── changes/
│       ├── 002-create-dynamic-query-tables.xml
│       └── 003-insert-initial-query-data.xml
└── application.yaml
```

## 🗄️ Schema do Banco de Dados

### Tabela: query_base
```sql
CREATE TABLE query_base (
    id UUID PRIMARY KEY,
    name_unique VARCHAR(100) UNIQUE NOT NULL,
    context VARCHAR(50) NOT NULL,
    query TEXT NOT NULL,
    description VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);
```

### Tabela: query_filter
```sql
CREATE TABLE query_filter (
    id UUID PRIMARY KEY,
    name_unique VARCHAR(100) UNIQUE NOT NULL,
    context VARCHAR(50) NOT NULL,
    query_fragment TEXT NOT NULL,
    description VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);
```

### Tabela: query_order
```sql
CREATE TABLE query_order (
    id UUID PRIMARY KEY,
    name_unique VARCHAR(100) UNIQUE NOT NULL,
    context VARCHAR(50) NOT NULL,
    query_fragment TEXT NOT NULL,
    description VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);
```

## 🔐 Segurança

### Parâmetros Nomeados
O sistema utiliza parâmetros nomeados do JPA para prevenir SQL Injection:

```sql
-- ✅ Correto - Usando parâmetro nomeado
AND name LIKE :name

-- ❌ Errado - Concatenação direta (vulnerável)
AND name LIKE '%John%'
```

### Validação de Queries
O endpoint de preview executa queries em modo read-only e com limite de resultados para prevenir:
- Queries maliciosas
- Consumo excessivo de recursos
- Modificações acidentais nos dados

## 🧪 Testing

### Teste Manual via Interface
1. Acesse a tab "Query Preview"
2. Monte sua query com filtros e ordenações
3. Adicione parâmetros de teste
4. Clique em "Preview Query"
5. Verifique a query final e os resultados

### Teste via API
```bash
# Testar query preview
curl -X POST http://localhost:8080/api/admin/queries/preview \
  -H "Content-Type: application/json" \
  -d '{
    "baseQuery": "SELECT * FROM customers WHERE 1=1 {{filters}}",
    "filters": ["AND name LIKE :name"],
    "orders": ["ORDER BY name ASC"],
    "parameters": {"name": "%John%"}
  }'
```

## 📝 Casos de Uso

### 1. Sistema de Busca Avançada
Combine múltiplos filtros para criar buscas complexas sem alterar código.

### 2. Relatórios Dinâmicos
Crie relatórios personalizáveis onde usuários podem escolher filtros e ordenações.

### 3. API Flexível
Exponha queries dinâmicas via API permitindo que clientes escolham seus filtros.

### 4. Multi-tenancy
Use contextos diferentes para isolar queries de diferentes tenants.

## 🎓 Próximos Passos

Para melhorar ainda mais esta POC, considere:

1. **Autenticação e Autorização**: Adicionar segurança ao painel admin
2. **Versionamento de Queries**: Manter histórico de alterações
3. **Cache**: Implementar cache para queries frequentes
4. **Validação Avançada**: Validar sintaxe SQL antes de salvar
5. **Auditoria**: Log de quem criou/alterou cada query
6. **Export/Import**: Exportar configurações de queries para JSON
7. **Templates**: Criar templates de queries pré-configuradas
8. **Testes Automatizados**: Unit tests para o service layer

## 📞 Suporte

Para dúvidas ou sugestões:
- Consulte a documentação integrada na tab "Documentation"
- Use o Query Preview para validar suas queries
- Verifique os exemplos neste README

## 📄 Licença

Este é um projeto de Prova de Conceito (POC) para fins educacionais e de demonstração.

---

**Desenvolvido com ❤️ usando Spring Boot, JPA e vanilla JavaScript**

