# Exemplos de Body para Teste - Endpoint /api/customers/search

## 📊 Dados de Exemplo Disponíveis no Banco

| ID | Nome | CNPJ | Ativo | Bloqueado |
|----|------|------|-------|-----------|
| 650e8400-e29b-41d4-a716-446655440000 | Empresa Silva LTDA | 11111111111111 | ✅ Sim | ❌ Não |
| 650e8400-e29b-41d4-a716-446655440001 | Empresa Santos e Cia | 22222222222222 | ✅ Sim | ❌ Não |
| 650e8400-e29b-41d4-a716-446655440002 | Empresa Oliveira ME | 33333333333333 | ❌ Não | ❌ Não |
| 650e8400-e29b-41d4-a716-446655440003 | Empresa Souza Bloqueada | 44444444444444 | ✅ Sim | ⚠️ Sim |
| 650e8400-e29b-41d4-a716-446655440004 | Tech Solutions LTDA | 55555555555555 | ✅ Sim | ❌ Não |

---

## 🧪 Exemplos de Requisições

## 1. Clientes Ativos (Ordenados por Nome A-Z)
```json
{
  "filters": ["only_customer_actives"],
  "order": "customer_order_by_name_asc",
  "parameters": {}
}
```

## 2. Clientes Bloqueados (Ordenados por Nome Z-A)
```json
{
  "filters": ["only_customer_blocked"],
  "order": "customer_order_by_name_desc",
  "parameters": {}
}
```

## 3. Buscar por Nome (parcial - "Silva")
```json
{
  "filters": ["customer_by_name"],
  "order": "customer_order_by_name_asc",
  "parameters": {
    "name": "Silva"
  }
}
```

## 4. Buscar por CNPJ (exato - Empresa Silva)
```json
{
  "filters": ["customer_by_cnpj"],
  "parameters": {
    "cnpj": "11111111111111"
  }
}
```

## 5. Buscar por ID (Tech Solutions)
```json
{
  "filters": ["customer_by_id"],
  "parameters": {
    "id": "650e8400-e29b-41d4-a716-446655440004"
  }
}
```

## 6. Clientes Ativos por Nome (parcial - "Santos")
```json
{
  "filters": ["only_customer_actives", "customer_by_name"],
  "order": "customer_order_by_name_asc",
  "parameters": {
    "name": "Santos"
  }
}
```

## 7. Clientes Bloqueados por Nome (parcial - "Souza")
```json
{
  "filters": ["only_customer_blocked", "customer_by_name"],
  "order": "customer_order_by_name_asc",
  "parameters": {
    "name": "Souza"
  }
}
```

## 8. Mais Recentes Primeiro
```json
{
  "filters": ["only_customer_actives"],
  "order": "customer_order_by_created_desc",
  "parameters": {}
}
```

## 9. Mais Antigos Primeiro
```json
{
  "filters": ["only_customer_actives"],
  "order": "customer_order_by_created_asc",
  "parameters": {}
}
```

## 10. Todos os Clientes (sem filtros)
```json
{
  "filters": [],
  "order": "customer_order_by_name_asc",
  "parameters": {}
}
```

## 11. Cliente Ativo por CNPJ (Tech Solutions)
```json
{
  "filters": ["only_customer_actives", "customer_by_cnpj"],
  "order": "customer_order_by_name_asc",
  "parameters": {
    "cnpj": "55555555555555"
  }
}
```

## 12. Busca Mínima (sem filtros nem ordenação)
```json
{
  "filters": [],
  "parameters": {}
}
```

---

## ✅ Resultados Esperados

| Exemplo | Resultado Esperado |
|---------|-------------------|
| 1. Clientes Ativos | 4 clientes: Silva, Santos, Souza Bloqueada, Tech Solutions |
| 2. Clientes Bloqueados | 1 cliente: Empresa Souza Bloqueada |
| 3. Buscar "Silva" | 1 cliente: Empresa Silva LTDA |
| 4. CNPJ "11111111111111" | 1 cliente: Empresa Silva LTDA |
| 5. ID Tech Solutions | 1 cliente: Tech Solutions LTDA |
| 6. Ativos + "Santos" | 1 cliente: Empresa Santos e Cia |
| 7. Bloqueados + "Souza" | 1 cliente: Empresa Souza Bloqueada |
| 8. Mais Recentes | 4 clientes ordenados por data DESC |
| 9. Mais Antigos | 4 clientes ordenados por data ASC |
| 10. Todos sem filtros | 5 clientes (todos) |
| 11. Ativos + CNPJ Tech | 1 cliente: Tech Solutions LTDA |
| 12. Busca Mínima | 5 clientes (todos) |

---

## Paginação (Query Params opcionais)
Adicione na URL para controlar paginação:
- `?page=0&size=10` - Primeira página com 10 itens
- `?page=1&size=20` - Segunda página com 20 itens
- `?sort=name,asc` - Ordenação adicional (opcional)

**Exemplo completo:**
```
POST http://localhost:8080/api/customers/search?page=0&size=10
```

