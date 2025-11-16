CREATE TABLE produto (
    produto_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    preco DECIMAL(19,2) NOT NULL,
    sku VARCHAR(50) NOT NULL UNIQUE,
    categoria_id BIGINT,
    criado_em TIMESTAMP NOT NULL,
    atualizado_em TIMESTAMP NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT chk_preco_nao_negativo CHECK (preco >= 0)
);
CREATE INDEX idx_produto_sku ON produto(sku);