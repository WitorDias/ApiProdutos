CREATE TABLE estoque (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    produto_id BIGINT NOT NULL,
    quantidade INTEGER NOT NULL,
    criado_em TIMESTAMP NOT NULL,
    atualizado_em TIMESTAMP NOT NULL,
    CONSTRAINT fk_estoque_produto FOREIGN KEY (produto_id) REFERENCES produto(produto_id),
    CONSTRAINT chk_quantidade_nao_negativa CHECK (quantidade >= 0)
);