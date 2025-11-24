CREATE TABLE tb_categoria (
    categoria_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(255) NOT NULL,
    parent_id BIGINT,
    criado_em TIMESTAMP NOT NULL,
    atualizado_em TIMESTAMP NOT NULL,
    FOREIGN KEY (parent_id) REFERENCES tb_categoria(categoria_id) ON DELETE CASCADE,
    CONSTRAINT uk_nome_parent UNIQUE (nome, parent_id)
);

CREATE INDEX idx_categoria_parent ON tb_categoria(parent_id);

ALTER TABLE tb_produto ADD CONSTRAINT fk_produto_categoria
    FOREIGN KEY (categoria_id) REFERENCES tb_categoria(categoria_id);
CREATE INDEX idx_produto_categoria ON tb_produto(categoria_id);

