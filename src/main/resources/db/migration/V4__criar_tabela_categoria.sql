CREATE TABLE categoria (
    categoria_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(255) NOT NULL,
    parent_id BIGINT,
    criado_em TIMESTAMP NOT NULL,
    atualizado_em TIMESTAMP NOT NULL,
    FOREIGN KEY (parent_id) REFERENCES categoria(categoria_id) ON DELETE CASCADE,
    CONSTRAINT uk_nome_parent UNIQUE (nome, parent_id)
);

CREATE INDEX idx_categoria_parent ON categoria(parent_id);

ALTER TABLE produto ADD CONSTRAINT fk_produto_categoria
    FOREIGN KEY (categoria_id) REFERENCES categoria(categoria_id);
CREATE INDEX idx_produto_categoria ON produto(categoria_id);

