-- ==============================================================================
-- Flyway V4: Atualização das senhas oficiais das contas de teste para BCrypt
-- Senha padrão para os usuários de desenvolvimento: password123
-- Hash BCrypt com 10 rounds: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
-- ==============================================================================

UPDATE customers
SET password = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'
WHERE email IN (
    'atleta@fittshop.com.br',
    'vendedora@fittshop.com.br',
    'estoquista@fittshop.com.br',
    'caixa@fittshop.com.br',
    'gerente@fittshop.com.br',
    'admin@fittshop.com.br'
);
