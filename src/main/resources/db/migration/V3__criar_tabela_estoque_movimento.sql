CREATE TABLE movimentacoes_estoque (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    produto_id BIGINT NOT NULL,
    quantidade INTEGER NOT NULL,
    tipo_movimento VARCHAR(15) NOT NULL,
    criado_em TIMESTAMP NOT NULL,
    CONSTRAINT fk_movimento_produto FOREIGN KEY (produto_id) REFERENCES produtos(produto_id),
    CONSTRAINT chk_quantidade_positiva CHECK (quantidade > 0),
    CONSTRAINT chk_tipo_movimento CHECK (tipo_movimento IN ('ENTRADA', 'SAIDA'))
);