-- Insert base query for customers
INSERT INTO query_base (id, name_unique, context, query, description, active, created_at, updated_at)
VALUES (RANDOM_UUID(), 'customer_base_query', 'CUSTOMER', 'SELECT c FROM Customer c WHERE c.deletedAt IS NULL', 'Query base para buscar todos os customers não deletados', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert filters for customers
INSERT INTO query_filter (id, name_unique, context, query_fragment, description, active, created_at, updated_at)
VALUES (RANDOM_UUID(), 'only_customer_actives', 'CUSTOMER', 'AND c.active = TRUE', 'Filtrar apenas customers ativos', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO query_filter (id, name_unique, context, query_fragment, description, active, created_at, updated_at)
VALUES (RANDOM_UUID(), 'only_customer_blocked', 'CUSTOMER', 'AND c.blocked = TRUE', 'Filtrar apenas customers bloqueados', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO query_filter (id, name_unique, context, query_fragment, description, active, created_at, updated_at)
VALUES (RANDOM_UUID(), 'customer_by_cnpj', 'CUSTOMER', 'AND c.cnpj = :cnpj', 'Filtrar customer por CNPJ', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO query_filter (id, name_unique, context, query_fragment, description, active, created_at, updated_at)
VALUES (RANDOM_UUID(), 'customer_by_id', 'CUSTOMER', 'AND c.id = :id', 'Filtrar customer por ID', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO query_filter (id, name_unique, context, query_fragment, description, active, created_at, updated_at)
VALUES (RANDOM_UUID(), 'customer_by_name', 'CUSTOMER', 'AND LOWER(c.name) LIKE LOWER(CONCAT(''%'', :name, ''%''))', 'Filtrar customer por nome (busca parcial)', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert orders for customers
INSERT INTO query_order (id, name_unique, context, query_fragment, description, active, created_at, updated_at)
VALUES (RANDOM_UUID(), 'customer_order_by_name_asc', 'CUSTOMER', 'ORDER BY c.name ASC', 'Ordenar customers por nome crescente', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO query_order (id, name_unique, context, query_fragment, description, active, created_at, updated_at)
VALUES (RANDOM_UUID(), 'customer_order_by_name_desc', 'CUSTOMER', 'ORDER BY c.name DESC', 'Ordenar customers por nome decrescente', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO query_order (id, name_unique, context, query_fragment, description, active, created_at, updated_at)
VALUES (RANDOM_UUID(), 'customer_order_by_created_desc', 'CUSTOMER', 'ORDER BY c.createdAt DESC', 'Ordenar customers por data de criação (mais recentes primeiro)', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO query_order (id, name_unique, context, query_fragment, description, active, created_at, updated_at)
VALUES (RANDOM_UUID(), 'customer_order_by_created_asc', 'CUSTOMER', 'ORDER BY c.createdAt ASC', 'Ordenar customers por data de criação (mais antigos primeiro)', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert indicator rules for customers
INSERT INTO indicator_rule (id, rule_key, name, description, icon, context, condition_field, condition_operator, active, display_order, created_at, updated_at)
VALUES (RANDOM_UUID(), 'customer_is_blocked', 'Cliente Bloqueado', 'Indica se o cliente está bloqueado no sistema', 'lock', 'CUSTOMER', 'blocked', 'IS_TRUE', true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO indicator_rule (id, rule_key, name, description, icon, context, condition_field, condition_operator, active, display_order, created_at, updated_at)
VALUES (RANDOM_UUID(), 'customer_is_inactive', 'Cliente Inativo', 'Indica se o cliente está inativo', 'user-slash', 'CUSTOMER', 'active', 'IS_FALSE', true, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO indicator_rule (id, rule_key, name, description, icon, context, condition_field, condition_operator, active, display_order, created_at, updated_at)
VALUES (RANDOM_UUID(), 'customer_is_active', 'Cliente Ativo', 'Indica se o cliente está ativo no sistema', 'check-circle', 'CUSTOMER', 'active', 'IS_TRUE', true, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO indicator_rule (id, rule_key, name, description, icon, context, condition_field, condition_operator, active, display_order, created_at, updated_at)
VALUES (RANDOM_UUID(), 'customer_not_blocked', 'Sem Bloqueio', 'Indica que o cliente não possui bloqueios', 'unlock', 'CUSTOMER', 'blocked', 'IS_FALSE', true, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO indicator_rule (id, rule_key, name, description, icon, context, condition_field, condition_operator, active, display_order, created_at, updated_at)
VALUES (RANDOM_UUID(), 'customer_is_deleted', 'Cliente Deletado', 'Indica se o cliente foi deletado (soft delete)', 'trash', 'CUSTOMER', 'deletedAt', 'IS_NOT_NULL', true, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert sample customers for testing
INSERT INTO customer (id, name, trade_name, cnpj, active, blocked, created_at, updated_at)
VALUES (RANDOM_UUID(), 'Empresa ABC Ltda', 'ABC Comercio', '12345678000190', true, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO customer (id, name, trade_name, cnpj, active, blocked, created_at, updated_at)
VALUES (RANDOM_UUID(), 'Industria XYZ SA', 'XYZ Industrial', '98765432000111', true, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO customer (id, name, trade_name, cnpj, active, blocked, created_at, updated_at)
VALUES (RANDOM_UUID(), 'Comercio DEF ME', 'DEF Varejo', '11223344000155', false, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO customer (id, name, trade_name, cnpj, active, blocked, created_at, updated_at)
VALUES (RANDOM_UUID(), 'Servicos GHI Ltda', 'GHI Servicos', '55667788000199', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO customer (id, name, trade_name, cnpj, active, blocked, created_at, updated_at)
VALUES (RANDOM_UUID(), 'Tech JKL SA', 'JKL Technology', '99887766000144', true, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

