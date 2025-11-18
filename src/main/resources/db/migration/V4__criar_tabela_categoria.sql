CREATE TABLE categorias (
    categoria_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(255) NOT NULL,
    parent_id BIGINT,
    criado_em TIMESTAMP NOT NULL,
    atualizado_em TIMESTAMP NOT NULL,
    FOREIGN KEY (parent_id) REFERENCES categorias(categoria_id) ON DELETE CASCADE,
    CONSTRAINT uk_nome_parent UNIQUE (nome, parent_id)
);

CREATE INDEX idx_categoria_parent ON categorias(parent_id);

ALTER TABLE produtos ADD CONSTRAINT fk_produto_categoria
    FOREIGN KEY (categoria_id) REFERENCES categorias(categoria_id);
CREATE INDEX idx_produto_categoria ON produtos(categoria_id);

